@echo off
java -Xms1024M -Xmx1024M -cp ../config;../lib/*;../lib/ontoqlconsole-1.2.jar fr.ensma.lisi.ontoqlconsole.OntoQLConsole %1 %2 %3 %4 %5 %6
