package domain

import domain.mechanisms.*
import domain.objects.DynamicObject
import domain.objects.HHCellObject
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import java.io.File

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
    val dynamic = HHCellObject(HHSignals())

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }


    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100000 }

    println((dynamic.signals as HHSignals))
    println((dynamic.history.size))

    File("somefile.txt").bufferedWriter().use { out ->
        out.write("I;V;N;M;H\n")
        dynamic.history.forEach {
            it as HHSignals
            out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
        }
    }
}
