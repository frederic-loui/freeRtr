pack2��P�         �2�	+
dialup.cfg                                                                                                                                                                                                                                                     ;configuration...
debugMode 2                                      ;debug level...
setVariable6 "123456"                            ;phone number...
setVariable7 "user"                              ;username...
setVariable8 "pass"                              ;password...
setCurrentDir "\\temp\\"                         ;working directory...
setLogFile "\workPath\dialup.log"                ;log file...
;scriptFile dialup_single.mod
;scriptFile dialup_sharing.mod
quit
pack&� ov         �
�'9dialup4sharing.mod                                                                                                                                                                                                                                             echo "dialup v1.0, done by Mc at 2003-05-22 00:46:33."
echo ""

setPortLine 115200
setPortFlow 3
setPortModem 3
setVariable1 "0"
setCurrentExt ".dt"

dial:
calcVariable1 \var1\ + 1
echo "\datim\ - initializing..."
modemHangup 10
if \goodres\ <> 1 then goto initerr
modemReset
if \goodres\ <> 1 then goto initerr
modemDataMode
if \goodres\ <> 1 then goto initerr
echo "\datim\ - dialing (retry #\var1\)..."
startTimer
modemDial \var6\
if \goodres\ <> 1 then goto dial
echo "\datim\ - connected, string: \connStr\..."
portClose
execbg \\system\\communication\\hdlc.code "\driver\ \port\ $ffffffff"
setVariable1 \result\
echo "\datim\ - hdlc started, pid=\var1\..."
execbg \\internet\\kernel\\ppp.code "\var1\ u\var7\ p\var8\"
setVariable2 \result\
echo "\datim\ - ppp started, pid=\var2\..."
setVariable6 1
while \var6\ n< 16 do gosub delif
exec \\system\\process\\killprocess.code ipmask.code
exec \\system\\process\\killprocess.code tcp.code
exec \\system\\process\\killprocess.code dns.code
wait4up:
waitSomeTime 1
exec \\system\\process\\processexists.code \var2\
if \result\ <> 0 then goto kill
exec \\internet\\kernel\\ip4conf.code "iface add \var2\ ppp-\driver\-\port\"
if \result\ <> 0 then goto wait4up
exec \\internet\\kernel\\ip4conf.code "iface add eth4.code eth0"
SetCurrentFile \workpath\\newFile\
eraseFile \currFile\
exec \\system\\process\\capturerun.code "\currFile\ \\internet\\kernel\\ip4iface.code eth4.code"
openFileR \currFile\
readLnFile
gosub read
setVariable7 \readEd\
readLnFile
gosub read
setVariable8 \readEd\
closeFile
eraseFile \currFile\
echo "\datim\ - eth up, ip=\@\var7\\@ mask=\@\var8\\@..."
SetCurrentFile \workpath\\newFile\
eraseFile \currFile\
exec \\system\\process\\capturerun.code "\currFile\ \\internet\\kernel\\ip4iface.code \var2\"
openFileR \currFile\
readLnFile
gosub read
setVariable3 \readEd\
gosub read
setVariable4 \readEd\
readLnFile
gosub read
setVariable5 \readEd\
closeFile
eraseFile \currFile\
exec \\internet\\kernel\\ip4conf.code "route add 1 \var3\ 255.255.255.255 \var7\ \var8\ 0.0.0.0"
exec \\internet\\kernel\\ip4conf.code "route add 2 \var3\ 255.255.255.255 0.0.0.0 0.0.0.0 \var4\"
exec \\internet\\kernel\\ip4conf.code "route add 3 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0 \var3\"
waitSomeTime 5
execbg \\internet\\kernel\\ipmask.code "ip4.code \var7\ \var8\ \var3\"
waitSomeTime 1
execbg \\internet\\kernel\\tcp.code "ipmask.code"
waitSomeTime 1
execbg \\internet\\kernel\\dns.code "256 \var5\"
echo "\datim\ - ppp up, ip=\@\var3\\@ gate=\@\var4\\@ dns=\@\var5\\@..."
exec \\system\\process\\processwait.code \var2\
echo "\datim\ - ppp down..."
exec \\internet\\kernel\\ip4conf.code "route del 1"
kill:
exec \\system\\process\\killprocess.code \var1\
exec \\system\\process\\killprocess.code \var2\
portOpen
modemHangup 10
calcVariable1 \timepast\
calcVariable3 \var1\ % 60
calcVariable1 \var1\ / 60
calcVariable2 \var1\ % 60
calcVariable1 \var1\ / 60
echo "\datim\ - disconnected after \var1\:\var2\:\var3\ seconds..."
quit

read:
readLnFile
stringSearch ":" \readEd\
calcVariable5 \result\ + 1
stringGetPart \readEd\ \var5\ 255
stringUnplug \readEd\
return

delif:
exec \\internet\\kernel\\ip4conf.code "iface del 1"
exec \\internet\\kernel\\ip4conf.code "route del 1"
calcVariable6 \var6\ + 1
return

initerr:
echo "\datim\ - ERROR: failed to initialize modem, exiting!"
quit
pack��W�y
         �		� 2dialup4single.mod                                                                                                                                                                                                                                              echo "dialup v1.0, done by Mc at 2003-05-22 00:46:33."
echo ""

setPortLine 115200
setPortFlow 3
setPortModem 3
setVariable1 "0"
setCurrentExt ".dt"

dial:
calcVariable1 \var1\ + 1
echo "\datim\ - initializing..."
modemHangup 10
if \goodres\ <> 1 then goto initerr
modemReset
if \goodres\ <> 1 then goto initerr
modemDataMode
if \goodres\ <> 1 then goto initerr
echo "\datim\ - dialing (retry #\var1\)..."
startTimer
modemDial \var6\
if \goodres\ <> 1 then goto dial
echo "\datim\ - connected, string: \connStr\..."
portClose
execbg \\system\\communication\\hdlc.code "\driver\ \port\ $ffffffff"
setVariable1 \result\
echo "\datim\ - hdlc started, pid=\var1\..."
execbg \\internet\\kernel\\ppp.code "\var1\ u\var7\ p\var8\"
setVariable2 \result\
echo "\datim\ - ppp started, pid=\var2\..."
setVariable6 1
while \var6\ n< 16 do gosub delif
exec \\system\\process\\killprocess.code tcp.code
exec \\system\\process\\killprocess.code dns.code
wait4up:
waitSomeTime 1
exec \\system\\process\\processexists.code \var2\
if \result\ <> 0 then goto kill
exec \\internet\\kernel\\ip4conf.code "iface add \var2\ ppp-\driver\-\port\"
if \result\ <> 0 then goto wait4up
SetCurrentFile \workpath\\newFile\
eraseFile \currFile\
exec \\system\\process\\capturerun.code "\currFile\ \\internet\\kernel\\ip4iface.code \var2\"
openFileR \currFile\
readLnFile
gosub read
setVariable3 \readEd\
gosub read
setVariable4 \readEd\
readLnFile
gosub read
setVariable5 \readEd\
closeFile
eraseFile \currFile\
exec \\internet\\kernel\\ip4conf.code "route add 1 \var3\ 255.255.255.255 0.0.0.0 0.0.0.0 \var4\"
waitSomeTime 5
execbg \\internet\\kernel\\tcp.code "ip4.code"
waitSomeTime 1
execbg \\internet\\kernel\\dns.code "256 \var5\"
echo "\datim\ - ppp up, ip=\@\var3\\@ gate=\@\var4\\@ dns=\@\var5\\@..."
exec \\system\\process\\processwait.code \var2\
echo "\datim\ - ppp down..."
exec \\internet\\kernel\\ip4conf.code "route del 1"
kill:
exec \\system\\process\\killprocess.code \var1\
exec \\system\\process\\killprocess.code \var2\
portOpen
modemHangup 10
calcVariable1 \timepast\
calcVariable3 \var1\ % 60
calcVariable1 \var1\ / 60
calcVariable2 \var1\ % 60
calcVariable1 \var1\ / 60
echo "\datim\ - disconnected after \var1\:\var2\:\var3\ seconds..."
quit

read:
readLnFile
stringSearch ":" \readEd\
calcVariable5 \result\ + 1
stringGetPart \readEd\ \var5\ 255
stringUnplug \readEd\
return

delif:
exec \\internet\\kernel\\ip4conf.code "iface del 1"
exec \\internet\\kernel\\ip4conf.code "route del 1"
calcVariable6 \var6\ + 1
return

initerr:
echo "\datim\ - ERROR: failed to initialize modem, exiting!"
quit
