#!/bin/sh
java -Xmx256m -jar rtr.jar test tester p4lang- other p4lang2.ini summary slot 132 paralell 10 retry 16 url http://sources.nop.hu/cfg/ $@
