package domain.objects

import domain.responses.DynamicResponse

class HHCellObject : PhysicalObject() {
    // constants
    // mS/cm^2
    private val g_L = 0.3
    private val g_K = 35.0
    private val g_Na = 120.0

    // mV
    private val E_L = -54.387
    private val E_K = -77.0
    private val E_Na = 50.0

    // mF/cm^2
    private val C_m = 1.0

    // dt
    private val dt = 0.02

    private val pulseVal = 100.0
    

}