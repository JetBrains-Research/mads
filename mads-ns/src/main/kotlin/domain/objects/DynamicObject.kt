package domain.objects

import domain.Signals

class DynamicObject(override val signals: Signals) : PhysicalObject(signals) {

}