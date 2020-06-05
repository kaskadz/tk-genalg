package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.PopulationEvaluator
import pl.agh.edu.genalg.framework.metrics.Reporter

class QueensPopulationEvaluator(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationEvaluator<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun evaluateEntity(entity: Queens): EvaluatedQueens {
        val rowsWithQueens = mutableSetOf<Int>()
        val colsWithQueens = mutableSetOf<Int>()
        val neDiagonalsWithQueens = mutableSetOf<Int>()
        val seDiagonalsWithQueens = mutableSetOf<Int>()

        for (pos in entity.positions) {
            rowsWithQueens.add(pos.rowNum)
            colsWithQueens.add(pos.colNum)
            neDiagonalsWithQueens.add(pos.rowNum + pos.colNum)
            seDiagonalsWithQueens.add(BoardSize - 1 - pos.rowNum - pos.colNum)
        }

        val fitness =
            BoardSize * 4 - (rowsWithQueens.size + colsWithQueens.size + neDiagonalsWithQueens.size + seDiagonalsWithQueens.size)

        return EvaluatedQueens(entity, fitness)
    }
}