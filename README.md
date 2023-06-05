# mads
Multi-Agent Dynamic Simulation framework is designed in the discrete event system (DES) paradigm. Computes events in a parallel manner, synchronizes state, and updates conditions for following events to compute. Mads developed as a generalized version of [BCNNM](https://doi.org/10.3389/fncom.2020.588224) with causality or "condition first" principle, e.g., the event cannot directly trigger computing another event but change the state of the system in that way so conditions for computing other events will change. All model examples are made in the field of computational neuroscience. Framework designed in a manner that anyone can develop their own domain or expand an existing one.

## Components
* Computational core – contains queue for events, top level description of any simulation object, implementation of configuration, and logging system for dumping states and changes;
* NS domain – contains base implementation of three different neuron types with biophysical dynamics, synapses and transmitting mechanisms, input neurons and electrodes. 

## Examples
Examples of framework usages lie in 'mads-examples' module.
* 'current' – contains simple examples of one neuron and electrode for simulating constant current vs noise on membrane;
* 'circuits' – contains simple examples of two and three neurons respectful: input-excitatory and input-excitatory=inhibitory;
* 'population' – contains example of 1000 Izhikevich neurons with synapses and their dynamic with base thalamic noise,
* 'training' – contains examples of two networks of adaptive LIF and Izhikevich neurons with train procedure on MNIST dataset (prototypes).
