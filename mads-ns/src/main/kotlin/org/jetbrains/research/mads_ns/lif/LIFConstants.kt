package org.jetbrains.research.mads_ns.lif

import org.jetbrains.research.mads.core.types.Constants

object LIFConstants : Constants {
    const val tau_mem       = 20.0
    const val E_leak        = -60.0
    const val V_reset       = -70.0
    const val V_thresh      = -50.0
    const val Rm            = 10.0

    // dt
    const val dt = 0.01

}