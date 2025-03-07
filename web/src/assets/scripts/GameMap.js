import { GameObject } from "./GameObject";
import { Wall } from "./Wall";
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

        this.nxp = 0
        this.cid = 1
        this.selectX = -1
        this.selectY = -1
        this.op = 0

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
        if (this.store.state.record.is_record) {
            let k = 0
            let idx_a = 0, idx_b = 0

            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const winner = this.store.state.record.record_winner;
            const [cp0, cp1] = this.cps;
            const interval_id = setInterval(() => {
                if (idx_a >= Object.keys(a_steps).length || idx_b >= Object.keys(b_steps).length) {
                    if (winner === "all" || winner === "A") cp0.status = "end"
                    if (winner === "all" || winner === "B") cp1.status = "end"
                    clearInterval(interval_id)
                }
                if ((k > 0 && k % 2 === 0) || k === 1) {
                    // cp1
                    const opt = JSON.parse(b_steps[idx_b])
                    cp1.push_chess(
                        opt.op, opt.x, opt.y, opt.nx, opt.ny
                    )
                    ++idx_b
                }
                else {
                    // cp0
                    const opt = JSON.parse(a_steps[idx_a])
                    cp0.push_chess(
                        opt.op, opt.x, opt.y, opt.nx, opt.ny
                    )
                    ++idx_a
                }
                k ++ 
            }, 1000);
        } else {
            const that = this.store.state.pk.gameObject
            this.ctx.canvas.focus();

            this.ctx.canvas.addEventListener("click", e => {
                const rect = this.ctx.canvas.getBoundingClientRect()
                const cX = rect.left, cY = rect.top
                const pos = {
                    x: e.clientX - cX,
                    y: e.clientY - cY
                }
                let y = parseInt(pos.x / this.L), x = parseInt(pos.y / this.L)
                let nx = 0, ny = 0;

                if (this.nxp === this.cid && x >= 0 && x < this.rows && y >= 0 && y < this.cols) {
                    if (that.op === 0) {
                        this.store.state.pk.socket.send(JSON.stringify({
                            event: "move",
                            opt: {
                                op: 0,
                                x: x,
                                y: y,
                                nx: nx,
                                ny: ny,
                            },
                        }));
                    }
                    else {
                        let f = true
                        const thecp = that.cps[this.cid]
                        for (let i in thecp.cells) {
                            const r = thecp.cells[i].r, c = thecp.cells[i].c
                            if (r === x && c === y) {
                                that.selectX = x
                                that.selectY = y
                                f = false
                                break
                            }
                        }
                        if (f) {
                            nx = x
                            ny = y
                            that.store.state.pk.socket.send(JSON.stringify({
                                event: "move",
                                opt: {
                                    op: 1,
                                    x: that.selectX,
                                    y: that.selectY,
                                    nx: nx,
                                    ny: ny,
                                },
                            }))
                        }
                        
                    }
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
                if (r == this.selectX && c == this.selectY) {
                    this.ctx.fillStyle = "rgba(0, 0, 0, 0.2)"
                    this.ctx.arc((c + 0.5) * this.L, (r + 0.5) * this.L, this.L / 2 * 0.8, 0, 2 * Math.PI)
                    //this.ctx.globalCompositeOperation = 'destination-out'
                    this.ctx.fill()
                }
            }
        }
    }
}
