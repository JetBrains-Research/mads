# mads
Multi-Agent Dynamic Simulation framework is designed in the discrete event system (DES) paradigm. Computes events in a parallel manner, synchronizes state, and updates conditions for following events to compute. Mads developed as a generalized version of [BCNNM](https://doi.org/10.3389/fncom.2020.588224) with causality or "condition first" principle, e.g., the event cannot directly trigger computing another event but change the state of the system in that way so conditions for computing other events will change. All model examples are made in the field of computational neuroscience. Framework designed in a manner that anyone can develop their own domain or expand an existing one.

## Components
* Computational core
* NS domain
* Experiments
