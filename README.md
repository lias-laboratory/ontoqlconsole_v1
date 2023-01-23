# This repository is for OntoQLConsole 1

You are looking at the repository of OntoQLConsole for OntoDB version 1.

Please refer to [OntoDB repository](https://github.com/lias-laboratory/ontodb) to have a big picture of OntoDB ontology based database tool.

## Software requirements

* [OntoDBSchema V1](https://github.com/lias-laboratory/ontodbschema_v1)
* Java >= 8
* Maven (for compilation step)

## Compilation

* Compile the project.

```
$ mvn clean package
```

## Usage

We suppose that OntoDBSchema V1 is correctly installed. If not, please refer to this [page](https://github.com/lias-laboratory/ontodbschema_v1).

* Unzip the _ontoqlconsole-1.3-SNAPSHOT-dist.zip_ archive.

* Go to the _bin_ directory

* Execute the _ontoqlconsole.bat_ script or execute the following command

```console
$ java -Xms1024M -Xmx1024M -cp "../config:../lib/*:../lib/ontoqlconsole-1.2.jar" fr.ensma.lisi.ontoqlconsole.OntoQLConsole 
```

You need to specify the command paramters

* 1 : Host name (example : lisi-oracle)
* 2 : Port value (example : 5432)
* 3 : Database User name (example : postgres)
* 4 : Database Password (example : postgres)
* 5 : Database name (example : template_ontodb_continous)
* 6 : OntoQL filename (example : d:/test/sample.ontoql)

## Software licence agreement

Details the license agreement of OntoQLPlus V1: [LICENCE](LICENCE)

## Code analysis

* Lines of Code: 234
* Programming Languages: Java

## Historic Contributors (core developers first followed by alphabetical order)

* [Mickael BARON(core developer)](https://www.lias-lab.fr/members/mickaelbaron/)
* [Yamine AIT-AMEUR](https://www.lias-lab.fr/members/yamineaitameur/)
* [Ladjel BELLATRECHE](https://www.lias-lab.fr/members/bellatreche/)
* [Stéphane JEAN](https://www.lias-lab.fr/members/stephanejean/)
* [Guy PIERRA](https://www.lias-lab.fr/members/guypierra/)
