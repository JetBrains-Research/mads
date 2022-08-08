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
    createDynamicExperimentMultipleI()
}

fun createDynamicExperiment()
{
    val I_exp = 8.0
    val dynamic = HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6))

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }

    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100000 }

    var fname = "i_${I_exp}.txt"

    File(fname).bufferedWriter().use { out ->
        out.write("I;V;N;M;H\n")
        dynamic.history.forEach {
            it as HHSignals
            out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
        }
    }
}

fun createDynamicExperimentMultipleI() {
    for(iv in 2 .. 30 step 1)
    {
        println(iv)
        val I_exp = iv / 2.0
        val dynamic = HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6))

        val config = Configuration()

        val pathwayDynamic: Pathway<HHCellObject> = Pathway()
        pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
        pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
        pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
        pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }
        pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { it.forCondition }


        config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

        val s = Model(arrayListOf(dynamic), config)
        s.simulate { it.currentTime() > 100000 }
//
//        println((dynamic.signals as HHSignals))
//        println((dynamic.history.size))

        var fname = "i_${I_exp}.txt"

        File(fname).bufferedWriter().use { out ->
            out.write("I;V;N;M;H\n")
            dynamic.history.forEach {
                it as HHSignals
                out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
            }
        }
    }


}
