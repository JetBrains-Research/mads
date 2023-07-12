package org.jetbrains.research.mads.examples.spatial.diffuse

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.configuration.structure
import org.jetbrains.research.mads.core.lattice.Lattice
import org.jetbrains.research.mads.core.types.*

class Space(lattice: Lattice, vararg signals: Signals) : ModelObject(*signals) {
    init {
        this.type = "space"
        this.lattice = lattice
    }
}

class Cell(coordinate: Int, type: String, vararg signals: Signals) : ModelObject(SpatialSignals(coordinate, 1.0), *signals) {
    init {
        this.type = type
    }
}

class DiffusibleSignals(A: Double = 0.0, B: Double = 0.0): Signals() {
    var A: Double by observable(A)
    var B: Double by observable(B)
}

val diffuseConfig = configure {
    timeResolution = millisecond
    addPathway(pathway<Cell> {
        timeResolution = second
        mechanism(mechanism = Cell::signalOut) {
            duration = 5
            condition = {
                it.type == "spread"
            }
            constants = TransferSignalConstants(
                signal = DiffusibleSignals::A,
                fractionFn = { it * 0.35 }
            )
        }
        mechanism(mechanism = Cell::signalIn) {
            duration = 1
            condition = {
                it.type == "gather"
            }
            constants = TransferSignalConstants(
                signal = DiffusibleSignals::A,
                fractionFn = { it * 0.5 }
            )
        }
    })
    addPathway(pathway<Space> {
        timeResolution = millisecond
        mechanism(mechanism = Space::diffuse) {
            duration = 200
            constants = DiffuseConstants(
                signal = DiffusibleSignals::A,
                rate = 1.0
            )
        }
    })
}

val diffuseStructure = structure {
    node(Space(Lattice(10, setOf(DiffusibleSignals())), DiffusibleSignals())) {
        node(Cell(222, "spread", DiffusibleSignals(100.0)))
        node(Cell(888, "spread", DiffusibleSignals(100.0)))
        node(Cell(444, "gather", DiffusibleSignals(0.0)))
    }
}