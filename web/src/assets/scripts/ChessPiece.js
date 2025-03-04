import { GameObject } from './GameObject'
import { Cell } from './Cell'

export class ChessPiece extends GameObject {
    constructor(info, gamemap) {
        super()
        this.id = info.id
        this.color = info.color
        this.gamemap = gamemap
        this.op = 0

        this.cells = []
        this.status = "live"
        
        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }

    start() {}

    push_chess(op, x, y, nx=0, ny=0) {
        console.log(op, x, y)
        if (op === 0) {
            this.cells.push(new Cell(x, y))
        } else {
            for (let i in this.cells) {
                const cell = this.cells[i]
                if (cell.r === x && cell.c === y) {
                    this.cells.splice(i)
                    break
                }
            }
            this.cells.push(new Cell(nx, ny))
        }
    }

    update_move() {
    }

    update() {
        this.render()
    }

    render() {
        const L = this.gamemap.L
        const ctx = this.gamemap.ctx
        
        ctx.fillStyle = this.color
        if (this.status == "end") {
            ctx.fillStyle = "white"
        }

        for (const cell of this.cells) {
            ctx.beginPath()
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2)
            ctx.fill()
        }

        ctx.fillStyle = "black"
        for (const cell of this.cells) {
            for (let i = 0; i < 2; ++i) {
                const eye_x = (cell.x + this.eye_dx[cell.d][i] * 0.15) * L
                const eye_y = (cell.y + this.eye_dy[cell.d][i] * 0.15) * L

                ctx.beginPath()
                ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2)
                ctx.fill()
            }
        }
    }
}