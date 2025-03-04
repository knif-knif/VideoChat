import { GameObject } from "./GameObject";
import { Wall } from "./Wall";
import { Snake } from './Snake';
import { ChessPiece } from './ChessPiece'

export class GameMap extends GameObject {
    constructor(ctx, parent, store) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;

        this.rows = 8;
        this.cols = 8;

        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ]

        this.nxp = 0
        this.cid = 1

        this.cps = [
            new ChessPiece({id: 0, color: "#4876EC"}, this),
            new ChessPiece({id: 1, color: "#F94848"}, this),
        ]
    }

    set_nxp(nxp) {
        this.nxp = nxp
    }

    create_walls() {
        const g = this.store.state.pk.gamemap;

        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if (g[r][c] === 1) {
                     this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    add_listening_events() {
        console.log(this.store.state.record);

        if (this.store.state.record.is_record) {
            let k = 0;

            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) {
                    if (loser === "all" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k ++ ;
            }, 300);
        } else {
            this.ctx.canvas.focus();

            this.ctx.canvas.addEventListener("click", e => {
                const rect = this.ctx.canvas.getBoundingClientRect()
                const cX = rect.left, cY = rect.top
                const pos = {
                    x: e.clientX - cX,
                    y: e.clientY - cY
                }
                let x = parseInt(pos.x / this.L), y = parseInt(pos.y / this.L)
                let nx = 0, ny = 0;

                if (this.nxp === this.cid && x >= 0 && x < this.rows && y >= 0 && y < this.cols) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        opt: {
                            op: this.cps[this.nxp].op,
                            x: y,
                            y: x,
                            nx: nx,
                            ny: ny,
                        },
                    }));
                }
            });
        }
    }

    start() {
        if (this.store.state.pk.a_id == this.store.state.user.id) this.cid = 0 

        this.create_walls();

        this.add_listening_events();
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {  // 判断两条蛇是否都准备好下一回合了
        for (const cp of this.cps) {
            if (cp.status !== "live") return false;
        }
        return true;
    }

    check_valid() {  // 检测目标位置是否合法：没有撞到两条蛇的身体和障碍物

        return true;
    }

    update() {
        this.update_size();
        this.render();
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}
