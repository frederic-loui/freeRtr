packR�*��         ��	$door.mod                                                                                                                                                                                                                                                       ;configuration...
debugMode 2                                      ;debug level...
setCurrentDir "\\temp\\"                         ;working directory...
setLogFile "\workPath\door\port\.log"            ;log file...
setVariable8 "19791025"                          ;sysop password...

echo "door v1.0, done by Mc at 2003-05-22 00:46:33."
echo ""
setPortLine 115200

;------------------------------------------------
start:
echo "\datim\ - initializing..."
modemHangup 10
if \goodres\ <> 1 then goto initerr
setPortLine 115200
setPortFlow 3
setPortModem 3
modemDevice line
if \goodres\ <> 1 then goto initerr
modemReset
if \goodres\ <> 1 then goto initerr

echo "\datim\ - waiting for caller..."
modemWaitRing
startTimer
if \result\ <> 2 then goto vege
echo "\datim\ - caller arrived..."
if \voice\ <> 1 then goto data
ModemAnswer
ClearDetect
ModemPlay hallo.voc
ModemReadLn 7 1
echo "detected: busy=\mdmBusy\ dial=\mdmDial\ fax=\mdmFaxAsw\\mdmFaxCal\ data=\mdmDatAsw\\mdmDatCal\ silence=\mdmSilnc\ quiet=\mdmQuiet\ read=\@\readEd\\@"
if \mdmBusy\ <> 0 then goto start
if \mdmDial\ <> 0 then goto start
if \mdmFaxAsw\ <> 0 then goto fax
if \mdmFaxCal\ <> 0 then goto fax
if \mdmDatAsw\ <> 0 then goto data
if \mdmDatCal\ <> 0 then goto data
if \ReadLen\ > 0 then goto voice
if \mdmQuiet\ <> 0 then goto voice
if \mdmSilnc\ <> 0 then goto data
goto voice
;------------------------------------------------


;------------------------------------------------
vege:
echo "\datim\ - exit requested from console..."
modemHangup 1
modemCommand atz
quit
;------------------------------------------------

;------------------------------------------------
initerr:
echo "\datim\ - ERROR: failed to initialize modem, exiting!"
quit
;------------------------------------------------



;------------------------------------------------
passwd:
ModemPlay passwd.voc
ModemBeep 900 1
ClearDetect
echo "\datim\ - waiting for password..."
ModemReadLn 30 0 *#
if \ReadEd\ <> \var8\ then goto thanks
echo "\datim\ - sysop call detected..."
eraseFile \workPath\\currFile\
SetCurrentFile ""
SetCurrentFile \nextFile\
sysop:

ClearDetect
ModemReadLn 30 1
if \ReadLen\ <> 1 then goto start
if \ReadEd\ = 0 then goto start
if \ReadEd\ = 1 then goto sysop_prev
if \ReadEd\ = 2 then goto sysop_play
if \ReadEd\ = 3 then goto sysop_next

echo "invalid key (\readEd\) entered..."
ModemPlay sysop.voc
goto sysop

sysop_nomore:
SetCurrentFile \var1\
ModemBeep 1000 1
ModemBeep 900 1
ModemBeep 800 1
goto sysop

sysop_prev:
setVariable1 \currFile\
SetCurrentFile \prevFile\
if \currFile\ = "" then goto sysop_nomore
ModemBeep 900 1
echo "Current File: \currFile\..."
goto sysop

sysop_next:
setVariable1 \currFile\
SetCurrentFile \nextFile\
if \currFile\ = "" then goto sysop_nomore
ModemBeep 900 1
echo "Current File: \currFile\..."
goto sysop

sysop_play:
echo "playing \currFile\..."
ModemPlay \workPath\\currFile\ *#
ModemBeep 900 1
goto sysop
;------------------------------------------------



;------------------------------------------------
voice:
echo "\datim\ - voice call arrived..."
setCurrentExt ".voc"
ModemPlay leavemsg.voc
ModemBeep 1000 1
if \mdmBusy\ <> 0 then goto start
if \mdmDial\ <> 0 then goto start
if \mdmFaxAsw\ <> 0 then goto fax
if \mdmFaxCal\ <> 0 then goto fax
if \mdmDatAsw\ <> 0 then goto data
if \mdmDatCal\ <> 0 then goto data
ClearDetect
SetCurrentFile \newFile\
echo "Recording message to \currFile\..."
ModemRecord \workPath\\currFile\ 40 *#1234567890abcdef
ModemReadLn 1 0
if \ReadEd\ = "5" then goto passwd
thanks:
ModemPlay thanks.voc
goto start
quit
;------------------------------------------------



;------------------------------------------------
fax:
echo "\datim\ - fax call arrived..."
;ModemDataMode
setPortLine 19200
setCurrentExt ".fax"
SetCurrentFile \newFile\
echo "receiving fax to \currFile\..."
portClose
exec \\system\\communication\\modemFaxer.code "\driver\ \port\ cr f\workPath\\currFile\ l\logfile\ m1 i3612058528 s14400 d3 d3"
portOpen
goto start
;------------------------------------------------



;------------------------------------------------
data:
echo "\datim\ - data call arrived..."
ModemDataMode
setVariable7 \timepast\
startTimer
ModemAnswer
if \result\ <> 3 then goto start
setVariable7 \var7\+\timepast\
startTimer
echo "\datim\ - data connection: \@\connStr\\@... (\var7\ sec)"
binaryWrite "welcome to my home gateway!^M^J"
binaryWrite "connected at \connStr\ after \var7\ seconds...^M^J"
binaryWrite "the local time is \datim\.^M^J"
data_menu2:
binaryWrite "^M^J"
binaryWrite "0 - disconnect...^M^J"
binaryWrite "1 - start ppp...^M^J"
binaryWrite "2 - start login...^M^J"
binaryWrite "3 - little calculator...^M^J"
data_menu3:
binaryWrite "^M^J"
binaryWrite "choose: "

data_menu:
binaryReadLn 60 1 "" 1
if \readLen\ = 0 then goto data_timeout
if 0 @ \readEd\ then goto data_disconnect
if 1 @ \readEd\ then goto data_ppp
if 2 @ \readEd\ then goto data_console
if 3 @ \readEd\ then goto data_calc
if "\$7e\" @ \readEd\ then goto data_ppp_guess
goto data_menu

data_timeout:
echo "\datim\ - timeout, disconnecting..."
binaryWrite "timeout!"
data_disconnect:
stringSearch + \var7\
calcVariable1 \result\ - 1
calcVariable2 \result\ + 1
stringGetPart \var7\ 1 \var1\
setVariable1 \readEd\
stringGetPart \var7\ \var2\ 255
setVariable2 \readEd\
calcVariable1 \timepast\ + \var1\ + \var2\
calcVariable3 \var1\ % 60
calcVariable1 \var1\ / 60
calcVariable2 \var1\ % 60
calcVariable1 \var1\ / 60
binaryWrite "^M^Jdisconnecting after \var1\:\var2\:\var3\ seconds...^M^J"
goto start

data_console:
echo "\datim\ - caller chosen console..."
binaryWrite "starting console...^M^J"
portClose
exec \\system\\communication\\serialGateway.code "\driver\ \port\ termout-ansi.code \\system\\authentication\\login.code modem \\ \\utils\\shell.code"
portOpen
goto data_disconnect

data_ppp:
echo "\datim\ - caller chosen ppp..."
data_ppp_run:
binaryWrite "starting ppp...^M^J"
portClose
execbg \\system\\communication\\hdlc.code "\driver\ \port\"
setVariable1 \result\
echo "hdlc started, pid=\var1\..."
execbg \\internet\\kernel\\ppp.code "\var1\ serv l10.1.0.50 r22.33.44.55 d10.1.0.50 n10.1.0.50"
setVariable2 \result\
echo "ppp started, pid=\var2\..."
data_ppp_j1:
waitSomeTime 1
exec \\system\\process\\processexists.code \var2\
if \result\ <> 0 then goto data_ppp_j2
exec \\internet\\kernel\\ip4conf.code "iface add \var2\ ppp-\driver\-\port\"
if \result\ <> 0 then goto data_ppp_j1
data_ppp_j2:
exec \\system\\process\\processwait.code \var2\
exec \\system\\process\\killprocess.code \var1\
exec \\system\\process\\killprocess.code \var2\
portOpen
goto data_disconnect

data_ppp_guess:
binaryReadLn 1 20 "" 1
if "\$ff\\$7d\\$23\\$c0\" @ \readEd\ then goto data_ppp_guess2
if "\$ff\\$03\\$c0\" @ \readEd\ then goto data_ppp_guess2
goto data_menu
data_ppp_guess2:
echo "\datim\ - saw hdlc, caller running ppp..."
goto data_ppp_run

data_calc:
binaryWrite "^M^Jexpression: "
binaryReadLn 60 666 ^M^J
stringUnplug \readEd\
binaryWrite "^Mexpression: \@\readEd\\@^M^J"
scriptCommand "calcVariable5 \readEd\"
binaryWrite "result: \@\var5\\@^M^J"
goto data_menu2
;------------------------------------------------
pack��w��         � 1�5	hallo.voc                                                                                                                                                                                                                                                      ��x��x��x�x�w����xh���w������Vg��xvX�h�W�y�7��X��V�vu{U�hf��j��y�'���8{�j��j�'��k�H��i�g���G��8�z�E�Y�E�sjzFǖWW�Yxb�Iww�T��vIz�K��Xx���&��Xyy�Y�WY�G��y��H�6��k���c}�h��wy�i���yw���wx�x�h��x�w�w�gx�x�h�x��wh�vz�h�x�x�h�wy�x�w�hw��x���h��x��x�x���w�h�g���h���gw��h�h��y�wx�xyw���wxx�y�h��x�w��vx�vy�H�x���w�x�W�9�x�u���h��y�h�wx�w�y�h��y���g�xw���h��wx�w�g��ixx��w��wy�h��g�w��g��X��yx�wz�x���h���J�w���x�fz��g�g��yg�x�wx�vy�xw�wy�g��h�y�w�y��g�h��y�vz�vy�h����v�x�x�wx�g��x�i���x�W��X�vi�h���i�wz�wx�h�w��x��w��xy�wg�y�vy�x��xx�H�gy��i�wxh���xx�x�f����f�x�g����i�x�Y�w��i�wi�ey�i�vxx�g��yvw���x�X��k�v�y�j�v��xh�wxg��wy�vy��i�x�vy��h��j�X��iv�X�w��W��i��g�w�h�h�y���x�h�u{�xx�h�h�w�i�wxx�w�W��i�wv��i��y�x�xy��Z�fw�vY�H�vx��X�v�xwY�g��X��xh��X���W���F��xv��i�vy�uW�hh�vy�uz�w�wx��x�g��yy�W��vx�i�wh��h�Wz�i��g��z�hj�ix�w�wwy�h���wh�x�W��v��h�z�i�ey��f�wj�h�w��w�h�W��i�x�wx�X���vi���i��wkvy�i��h��g��i�xv��H��g���i��Z��y�y�X�vy�yxwh�uz�ww�vX���U�g�V��x�j�U�zT��wg�V����wY��uI���$y�k�k�F�F��y�5؉xE��yC���HE���"��Yc���W#��Ic��X�Zc�Y��hH���4w��5w��j�E��[t�x�E��Hc��W�U�z�wH���wgH��FQ�WD��hH�t�yFrۉ5��g3ȩG��i�z�4���4g�JVŜGb��F�Huכ��{P��Gt��fI���CV�yhq��WT��Yfb��W��b��e��zE�ݜ$e��XRw�xe�k�#��iT��Z��L@t�{4��yT��Grw����9����ʋEs̛$t��wUvv��FR�WV�Yt��\$��y����fDdګG4�z#u�h5cػY��ZR��yux%��k�C@��FBfdc��h�%t��xx$TɬH�U%��&TvEUE��yۉ�ʊgvET�HC��ywB4gT��w�yA�̉wwEc۫V�E3��3UUQ�͚ɋtɛ��&DǬyvES��EC4SRܺ��gQ����z2t���i$R��UE$Sؼ��XB�����%c����7Cuwuy%2����%u�g��iDv�eٌ5ewEu�%�̋�6T�i�܊5uyE��hEgBd�&Tݻv�z3��W��YT�Ga�zu�S�wEž���8c�zfʬFty%��zf�R�hWeڬ��yDu�F��he�Gcʛf�Q�xWdȼ���Fc�h�܋Euh$��h��e�hU����ݚ5d�h�͊Eug4��z�jS�xEht�ߚ�y4d�X�͋Euf&��zU�J@d�GT����މET�X�ܜV�w7s��f�w2e�geꬋ�g3��h�̋Eew4t܊gvfDDfWAD���٭XRf�g�۬Wfu&d�ˊUF33���Seۺ���FS�xW�ܛWcwxC��y%T�w4wɊV���fT��We�̉FUwgd���5UdTg�WVb���y5Dv�xw꼊3D��i���BD��yc��ڜg4Du����Ί4BU���yTFS33���iq��XCS���x���($d���YBSf�V5�������G$Sw������Ed�˫H1Tu���Y�VPd��zGBT�������5BBw���7ATw��̭�G43e����x��($c��xvvy���16���λj63Du����ʉ9DQf�xj�Fdg���ͪi53d����j�z�ST��]�5�b7��H��ϺiC3Dv˨��i��V4f��zvg�i٬ͻi43Du����iɉ93cW���V27ufuE�ͻjT2Eu�����ˊ%#Se���5bw��۾�jEASv������J31e���X#4eug�ܾ�iD2T�����̘(4DT��jCA4u�ܼ��iEQT������YSEeg�f#3Eu�ݼ��WDR���zCD����eFFTRVf$U�̚hESw�{xE�GUX��4�FFR��'���ʉXbV��ju3�y�W��,hQC�����Z�z�g#e��Zwd�gx�G��62���C����ݽHTs���ddk�6G�˜ERU�YgC�(t������jWs���hVRf�7'es��,T�̚D6�zhtɚ�x7���kTQ��WA��i7���wygh��T�{�]eF�i$��j��]�rv��M����fe��la��%v������)��jV4X�F&�{�f��5��n�eh��E�g��h�Ck��H�i�\����F�z�CX�v+�z��Z�c:Y�:�uj���HX��*��m�d���5�x�sy�H��bZ�8��J�~�F�)�eY��{�8�6ȉG�~�(��nft��T��.�u�G�s�vY���hD��*�u��9�i�֤&�G�R��(���z:�T�dlxC�r\��gyDv��sf}�*���;U��JJ�W��GI�Vh���z�x�W��zwwJ�v&��L���iiY�iJ�ZȄ<��h��e�]�GK��j���v���rX��h�pm�H�U{�ez�Do�Dy��xe��}ui�;�F��X{�7�G��i��Xh���rJ�7��x�5���~Gw�h�vUI��z�{T�^Ņ��I�u�Ku��wXgI�*�k�Z�f�S��(��h\�G���l�N�cyx�]�7�I�B�Y�Q��x$�v[�v�y�H�5Ɨ]�s~�W��G�tj���9x��(h�~�Y�}�<�F�u�hH��K�Yq|�E\�el�:ňheW�he[�K�i����dz�WxV���	���ej��e^��4�s[�q��+d|�eI��7�s��iV���uv�F+��m�Gl�j�(x6��[4�pack�琢         � 1� 1leavemsg.voc                                                                                                                                                                                                                                                   ��x��x��x�x�w�����wx�����wggw�ww�wxg��yh��iz�gxx�g�Uz��wV��Zwz�i�i��vW�hhf�w�v�v�i�g�X��yv�Yww�X��yh�ty�xy�g����WY���[�&�v�tyH���Vj�{v����hy�7�vju�U�����x�X�W�v�Xi�xg���gy��x�g��x�xx�fz�xw��i�xwx��yf�x�f�w�h�g��h�w�x�v�w�X��x�w��g�hx�v����X�X�g�xx��w�g�w��i��h�h�h��zv�v�vh��X�i�X�g�w�g�w��h����h�w�w��w���g�gj�x�xxw��h�w�X����wx��g��zv��wx�h�x�ui�hg�y�g�W���h�h�xyv�x�yi�h����g�y�e{�x�Y�xg�x�f�W�x�ux�xh��j��i�i�e�y��i��X�x��h��xw��v�w�g�x��x��X���f�h�u�Xw��yg�x�w�wx�f�tkx�v�Y�I�w�Ww��ex�wX��X�h��h�I�F��i�v��ii��w�g�G���Uyy�g�wyv�h�v�V�v��Wz�e��wf��vw���h�vxxw�xh�f��gxx�y�g��x�gx�zw��w���X�f�g�W��X��xV�y�xX�xh�vyx�g����g���h��W��hx��Y�w��i��Y��V��|V���V�Z�h�uj�Y�V�w��Y�g�h�V�X�f�f��j��G���vyx���Vix��[�w�x�zf�V�X�6��h��X���I�Y���wg�v�h|�g�8�vw�h�x�hx�x�W�H�vi�v��8�w���G�f�E��Z�ww��Yv�f�xf��H�w�g���E�Z�U�G�d�Vz�f��G�e�G����J���z�j�f��W�g�v�U�h�X��ix��xX�g�V�xzvv�iw�i���V�T���G��xWz�8�fy�y�gw�v�(��hj�wW�i�f��zY��y�I�gJ�f�H�U�u�f�dzy�F��{F�xg����Wh�Jk�W��i��gZ�x�W���I��H���Wf��U�Hy��f��Y�hx���5���6�vI�v�g��k���U�t�v�xY�U��H�J�fi�G�Y�7�U�h��Xvy�c�f�v�G�fI��y�G��Z�Xy�V��Wxh�v��G�w�iW��u�Gw��V�k�4��}ft�K�G�(�w�g{�:Շ�8�{�5�C�Wxx��I�jvw����U�wxxi��f��f���z�vxX���'�y�5���F��Iz��Iw�w���7�w�D�u��Y��W���Zt��=�W��hy�vux��7��I�Z�$�F��[�hvw�x�H���8��E�Y��V��hkd��+��{�wK�L�V�&��[�uh����V��I��I�d�ij��8�K�T�Uk�h�z�U�x�K�f��W�Y�Vy��q[��I�u��i�D}��i��kv��+�'��h��xZ��I���8�F��Y��I�;�E�Fvl�vj�Z���F�vh�H�&�s�dz�9�i�;�8�i�UH��J��W��i�Wv�X�H�u�7�G�F��+ƊF�V�C��z�k�rh�7�(�D{���gD����Wfv��6S��z�FD�͚fU��f�y5C��c��g0��IC���wW3�˚WUe��XT��h�8Cd�Gs��HS����FB�˚ffS��yWDT��Ru�g3��{4����i$d��gvCT��IB��wV3Dfe��Z4����Y3uˊggDt��IR��xF$TVf2�Y4����iB�˚wgEs��IC�zgD4CDUr��5d����6B��yvvE��Y4��VTE3SfT��6d����IA�ˊg���uUwTTv��%$��iTw�f��&4����YBt�hBCTv��W�}Vv�y���eW����&4ufgu�v3U�ۿ�5u��v���W7�|�C���DE�茩�Hc��wVw�'�cG�u�[��h�u���*���wc��e6��3j���[$�whx�M��C�X��xwK�D�bji�m����C�W��$�{v��S�dh�u}�F�vh��S�c�ej��6�fڔ'j�\�X�h�4^��f{sɄ[<�Q�ez���gYU�ז�U�:x�dWӊ�3S��Xs�y��iCt��H�i3DT�6SUfch���ɭ�z��Ev3��%v�C�v��c�fs��y$gEV�zy�۽jȬ���gS1VEErhzg캷�_b��BW�fEw��7vi���&�J�V�:�W�H�d�T��Kbx�6��V���U���6�:�r{i��jk��^�U��K��k�\j�rj��9�
�e�Fi��qJ�x8�f��e�7�6�x�OĥZY���;��y:�9�U�TY�Xø5ԙ� ���+ty��f�WdV��x�v�V�y��tH~�}Sfw�x�iy�r�|ey�Z�:�(���UZ�bh�TΦ�h�He&��wh�v�idw�����G�&U�W˻x�ffk�iHavd7����xGu3f�����[QThʘ�kATDV�ۮ��WT%e��۪h�3Ug�z��4W@fF���y�WRWT���g�%Th՜���$DfC��윘�4�4u�����z2uXș��5b5ef�ݬ��Wf%Sfg����h�z�6fgb7b�5gd���ˊg�6F�3vug�x��XfX�xva[axuw��ܚv�Y�wT2di�'��}�h�[qUYaI���ݪg����He��Hȓl�x�:x��yz���9ǖjX�%g��rk�lw�,�W�WF�gl���}�h�:W�b�8f���'��[V�D[4}������i�}��w	�5��k�t�zt�%�x�R���x�:��W�bt�Bj�ƙ�xl�eB�H�R��Y�Z�����M�6�T�(���G�GƷi�D�ylZ���}V����	����v��vxx���4TT�F���ʚW�1UfU����Ȋ8�8�eW�GX�A���߉�yTjATV���鋧��7d�D�GbhQ�W�ܜ��WuSvf���̇�VD6Rhf�7�&4�V�����Xt%Dugȹ���yGP6DuXv�JqF�h�ݾ���Uh2e�U�i�ʧxi��b(cE8�D���k�ǾY�wzhxs�38��d����X��vd��#j�苺�~�yJ�3g2�s��j;v�X���uj��E�٬U�g�(�c�Fu�����t�GY��B��{wzê�I�f;��d3�eV�v�U|��b�FI�b���h~��w��I��7��yɒJ���W��yW|������U�h5�Xk�8�7�u�[�K�UZ��dy[�z�{�~�T�D;qX�le�x9�T��ri�g���:�U���D�s{ch�kfH�h��*�[�Uh�e��~v�g̘X�5�hSj��U�g�6j����	�6�Uz�y��~�K�7v�'�e��|�hUd2�bi�y��띹{�WD%bG�X׍�Z�Wf%s5�D������˛�XfCBTTf��ߩ��YU&RVefw����|�x�gvuE�U�yވ��jT6RG�E�w��퉻��Eg@Wcxu�ȭ�~�Iu$d4�Sy���ϸ��ydsFtW�j빺m�V8RDCfdi��Ꜻ��Gu2URwf������Ku3C6�Dw��˟ɉ�hu%E4uW�����w{e$4EbVh���ʊ�gg$DRVf��̜��je#45dTI���˙�fWB4DVu��ی��YEQC5edI��Ϻ��yv&DBUV���ۇzvEB5d5���ݻ���gV2DCev�ݹ��wX4CBfdg�����y�fg3URw����Y�4UBUTY��۾���Z�5D$�Wɚ���W$B%dW����{�y�fWD3Uv����6u#fU�u�罫��hgWgQj�G�s9XT��8�Eך|g�|wjǃg{W(�f���v�[�J�g�kX�7wL��Y����H�nt�D�T�g�D��I�G�F�v�8�E�4�tl�Y��[WՊE��0�f��<��J�U�6�4Ɨ�H�F��K�6�9�i�<����D�(��I�;�t�8�gu�;��Մm�i�6�Jӈ7�y���!�U\���d���8�6�e��+��V�M�s�c�X�E��Z�h�|'��<�iW�J��(�8��sX�\�N�G����5�%�I�bj�Ye��~E�8��:�v�gU��ux�V�v��F�5�WW�h�Mb�8�T���6�V�W�l�\U˄��w���2�E���8�xh������7}�X��]�T�=��2�8�I�J�8�X����i�V�dHPG6�i�J�
�zc�W�6��j�i�yRybgv�:�Y�gCxQ�S�fv�V���9uhU�J�[�cv�S�Id�6��f�E�BE���vzgaI�jex4�ھ�{s��v;�*te�z��r�EX@vG�4�ܝ�y�yw�%�$�D��ǋ�{�'BWQwT�׍ٚ��h�CFAWex�z̈�tI#S%�&�w��{�i�6T%cH�Y�˪�irVTus��خ���z�GB%dfu�ȮȌ�YfT%uF���컼��wg$3DTU�������vFA5SEtf���ۉ�ww%eRfexxΘ���fWa6dEuh��̻���gFBBEde��ۻ��XV24CTT��ͭۚ�xyF4BDDfhη���g8a5UTvXټ̬�{�GT1T4e���ګ�xWC3UUw��ʭ���Yc%SUt���ʬ��7$SRVtxɞښ�y�&u3eE�H뙼��5GA$dV�xݹ���wzC5BUd�z�����$UAeD�f��̙�w�3VBVD�t����zcGBUSg���ۛ��vHQ%UU�Y멬��&t#eEv���؜��wXR6SV�v����y�2fTg���ɝ��hw45Sfv��ܩ��%U1gdw�םȌ�y�v$fbX�i���{s'SUUff���ȋ�zwXE4Rg�����XU�5uwV��������f9SeD�Z�kU�3UgwsX�Z��j�i�wiu&�Wg��ew7ewibk�x���/�H�gy�8�V��:�7�E{�I����wy�ٝ�˖n�VW�7�f�0��H�2�I�s�v�b��~�����dz�qze�7�gV����9��H�,���)�Jg�@̃[5�h�D�M�����M���kW��f�
�Hv�dO��d\��E]�E9�*�uz�˕���j��B�V�S��x<�Fju�vh������l�W�e8�cjƺ=�I�'w�5xSY�H�؛̘��H7s5vWj���X�R�%�&�fgw����h�GvTfu���·�c�Rw3wV�E�H�ʻ�x�T%4SV��ں���tlaHrWewV�F�ʮ���iSE3�Vț뚩XW�0�C�e�5U#Se�ۭ��jS6Rfv��۫�xERDxT���d$CS��ϙ�Xv$3fu��ܪxx$T5�����V5#DfeX�̫�{CFAe�����gD2D��ܺ��Cw2�hv�7sˬɝfwER�wۚ��uE����z�83uF�idVR����5t8b����Ef$S�yڻHuXCc�u�F@f#��z�j$uW�̛��#UwfꚈ�u�U��E�T�5��x�h2�h��{�X3T��ܪWv$t�V�{2e5e�6����7c����VgVR����(tX���uGS3xuXDr�y�x%�x��{dgDs���z3����j%SFbwVB�͊�G3�yȬzU6S��ʫEgVa�g�6dFeB��Ήwf$����jSFc����h4�%��8c%CUFR�ܭy�U$����YeU4���ɪ(c�e�|4S�B�UI�Κ�igC�W��v�js���ɍ$��x4�(U�C�y�w�gDT�{��YSuxV�G�HSg�9Q�%T��ݬxwwDT����XER��ڝexC�e�bw�$���xx�fC���vvxwG��~��DZsIPeyEs����SEhuyS��H��xx��E�E92�5���늖��5U��7���U���D��2fw'R�6tڽ�Y��iBe��D��JC���~��Be�8AvFS�͋exw4���u�5$�����iC�Fu�AT4s�̜vWUTʛ��c����VUfʊyvU3eDǜ�ivUVŌ�wGa����EVS��luGF�9rG2s��y�Cv���V'R����$Uuک�EUDs8bV3Bӿf�Et���f�6t���Gch���\cVV�#v%T3�x�ge���gwGs�x�Gd7ǼX�%vwEVQgB��vzU�k�{f�����%�E�juYc�5�%fEpχzVtkՌgw6����%�u�Z�FcgCiR41�h�fe���vw7����6�i�xR�3e5s&5���XU�X�hwwb�y�I�[U�%�&EERVU���XE�W�yfhR���i�9�yVg1vD�h���J�u��Zw�{���WXbvFSTi�s���y�V�w�~�Id�F�Af#d�g�U������h�z�zeZ�(�Gde"�H�Yt�u��̈�X���yU�wvHC�G�]�z�J�y�Y�V|�[tx#�D��5�h��'�X�{�9�����gJ烊8V��X�II�Ty��=�Uv�:��*�v�h�^��3���X�J�{ʫCp{�D�rj�Yw�F�K�)�k�9g�g�c�Y�(����WK������tL�hvz�|A˸d7�z�&�x�\Ç7�E��d_r�X��wX�W{V�7�Y�G�i�9�C��h�E�Y&��[d�ec�s�4�e�I�G�D�V�G�HyZ����`J�vG�l�P�u�t�q�&��sk)�E��YF�n�Z���f^�H��zg�zVƉt7��}����4�d��Q���t�F�gv���i�7��J�{d�����y��w4�j�Z�7���&;��8t�T�Th��k�z�SI�=��ez�h^�F}��{�Em�8�ud�<����K����`�FJ�V��Y��:�X�zun�Z�(�Xh��I����L�Xv�$�W�9�m�J�X���}��8�9�U�U���`Z��t���;��uye�7�ZWy�8�'��:�x��Z�G�4DXr{����H�ag�X�u�+Qg5�|�z��jh`G�F�ɪ��keC$uU��ͫ�GV1ESeG��ۊ�fE$cW�����[�ETe�髼�hh$�'�VfvUHb�n�y��dJqH�����k�g3WrY�����\�5Cx�iښH��eu(�뉽x�Fu2uF�z��Wg6d%��m�V�4e#we�����v9B4Dw��ګ�iTDE���ͪ�hYbDRX�s���v�Q�6�5�E�4�8�G�U�k�x�g�%�%�T�F�Y�w��h�Sid$uXu�̿���iG�5uF��ڊ�j�w'W����z�<�Szt��ku���A�S�D�G�9�X��D�r�S�v��I�X�;�Y�u~�j�5�K�4�����HH�uy�l�f'�E�9�ej�x�4�|�&�yj���}wE֚X�wm�)��H�W�6���e^��G��ek���d�\d�m�gE��8��V�tz�8�5�V�q�V�6�C���Hkh���@]�Z�G��sJ\�&�V�&��k�(�5�r�6��G�wG�VK�E�s��m��u�H�c��9�uxy�Wl�Uz�S}v�V��e�M�L�i�x)�U�DI�eo�Hxh��6�b�uI|��um�V���y��k�*��8�Iz�\�Ux�+�g�:���+�I�WxzŅ,�F�ȅH�c�S�cK�r��k����'���\�k{�\�h�G�T�3�U�X��I�(�X�jd�W�Z��f�_��F�k�[q�F�Hu�:�8��s��S�D�Y��R�yt^�X�	�V���Xx�(�C��U�n���9���Y7�{J�E�>����IE�W�Y��gz�zd�����(�X��z��U�D��,��5�Z�'�F��Ju�W�m����B�&�E�Y�Dx�Vl�F�:�'�7�g����9�E��L���*�xye�#��&�����le�x�'��Y�d�uZ��|�F�ٶeHL��Y�(xZ�F�H�g�pack�ü��
         � 1� 1
thanks.voc                                                                                                                                                                                                                                                     ��x��x��x�x�w�����w���xw��wg�����xVw�www�wxw�x�g�yvx�vw�gi�wvw�y�I�x�f����h�x�Y��I���hxv���i�e�y�ii�h��j�F�h�U�z�x�x�h�g�Y�v�Z��wy�g�v�f���g�i�ww��W���v��xy���i�xxx��w�wh�vY���w�w��x���g�z�g�g�g�V�w�vw�h��Z�f�w�x�g���wxi�g�W�g���v��yv�w�v�x��w��h�x�h�j�f��yg��wz�wh�y�h���xh�v�x�i�g��yxx�y��xyg�x�u�vxx���f�h��x�g�f��h���g���f���f�W�u�g�w��yv��w���vi��h�vh�g�G�v�h��x�x�h�vi�h�h�V���U��yh�v�g�v�y�e��yu�viy��xwy�Yw�gw�i��h�dz�h�yg�vx��Wx�y�W�h�v�yxv�wgw�w��V�e[�h�W�i��f�i���X�xxh��g��g�y�f��x���xw�y���vX��fx�w��Vx�x�hzv��xx��hx��tzv�h��i��X���h�h�g�g�hww���Vw�xi�g�g��hg��w�ix�v�i�H�t�V�h�w�G�g��g��h�g�f��ygh��W���U�Z�W�F�w�wyv�v��h��Y�wxh�Y�V�h�ux��V�W�v�V�x��w�y��e�v�ww�h�gk�i�gw��F��ix�e�H�W�V��xW��Z��h�wy��W�v�f�ty{�d�V���g���Xu�xh��h��vX��F���We��W���F�z�W�h�X�f���fx�g��H��j��Z�G�Z�d��{e�y�D���X���8�D��Z��W�vzv�X�f�j�Vy��Vx��U{�i��f�ixw���X����ty��hy�x�xxj�8�X�W��w�x�g��X��gx�y�ei�{w�W�gv��8��Y�hxx�vf�g��Zez�g��T�k�G����H�gY���Fw��u�I�W�f�Hw�zgV��h�g\�x�g�y�iy�tjx�j�f�6�V�G�f�X��J��t��I�wi�X�5��jU��IJ�Tə�D�$�u�\�X��;�W\�Y��\�Vj�g�vK�8��iF�Y�gy�eW�Y�Ey�jg�yuJ��gZ��E��9sWv�k�Ht&��z�E$�Vݷ�@x��z�ˉYQx����GE����ue���zafy�z�ug��3�v��Hv6��j�����W�&����$���piv�7�h��b�v�[�:T�8�hvxb���8��XhDui�z���t(�����g��uyB�z�Z�}cg��$�wۼEiq����h��G�D�{�]D�9�D�ZPz�͛�(�����U���\b�w�9�J�6�ZP�fܻgHr���'�6���YQy��wv��f6�x$�g��J�4���GQz���x&�X�gig�kI@he�8��}sX�x���e��WYpj�kwd���9�:�y���u&�Y�yTgc���Tf5�i�8��c8�Ic���z�6�X�idYr��}vgD�H�F��VXaZ�j�̋��g�Wfwr��}tYd�9�6�׏d6�6�k��i�c���ewf���Z�5�KŨXݍUCT%�}׬ivT|��F�V�k�Y���v����53v5s��Z�dl��VhE���[�8�:�GιeDI�{��h��9��e�6���j�FvK�ܜe�6����i�vY���H�V�s��8�w֛�s5UF���hw�8�z�%�w�cz�G�w�W�RXS6���k�vIb��Z�[�D��8�ϨwDybV���i��Gcj�8�h�4��j蜋 v�WUS�˭�igE�Vf���Z�kf��Xgcyg6sg�ڿ�i�6�'�6���wW�Y8�8veD�9���l�;�X�I���4���t{sifVy�ls[�|�8�G�D����J�I�2�c�T����W�jvx�G�}S�to�f��S�t\�X�6��g�&�6�H��v��I�I��f�c�8ƝD�E���f�V�(�H��E|��I�Ɖ�ryZ�L�L��X�[BȒ8�U��G�u�W�b��bl���U�Q�b�4�U�J�[Wʅ�\���)�D�sjx�H�;�[�Vn�W��I���&;���g�Vo�i�'�v�iu��\�7z��;��;�c�ukF�8�zU�fL�$���uk�ji�8�M�Z��qyx�Y�:j̑uk�Un�d�&��\�eI�W�C����3{�xo�k�iV�W�U���SN�E�f��3~�h�Dm�fzG�UƘ�L�;j�VI�df{iׁ<�)��uk��6~vKԈ���Tl��)w��jf�7��j�Y�YK���G;�eX���S��S����:��H�W�W����6�F^�H�v��5�yD��\u�fvJ��[�Vkb�H���(��;���i�h�^��6�iV��y���[i�dIטH����Fn�ci��(��]w�f�&��X�H��Xw��C{v:��+y�K�eY�J��}�y�J|�u��Xz�v�8�i��c)�U�D���G�F�s��D��7��IyFn�E���R��TL�h;�7�s\��<�F�e�|s]�{4�W��Xh���g��9���j����a��(����]c�hYg�7��|uR��7��J�g�Z�L�ZY�v�f�]�4�X�az�[�Y��k�D�|�g�\�F�<�T���2�G�j�A�X�x�'�GY�wF���bK�c]�Y�(���Y���EI�զx�HW��w'�kisZ�E�c[ȈF�xz;ֆ8�u��`�Z�b���4�c;�f�V�e[��si���)v��5t��e�c�W������%y��V��tlwZ��G�;�9���Q�E�|���W+xG����%��iX�\����VؙXs��5�:�S�W�Xx�H�X���y�8�t�p}h�W��$�e�<�U��xs2����hf�F�EVʫBI�9�Y��z5��^V�V�U�)��m�8�X�h��6��[pack�e�	         � +!�&*toolkit.mod                                                                                                                                                                                                                                                    echo "ToolKit v1.0, done by Mc at 2003-05-22 00:46:33."
echo "parameter: \@\readEd\\@"

debugMode 2
setCurrentDir \\temp\\
setLogFile ""
setCurrentExt .voc
setCurrentFile ""

if \voice\ = 1 then goto start
echo "voice modem required..."
quit 1

start:
echo "initializing..."
modemHangup 10
if \goodres\ <> 1 then goto vege
setPortLine 115200
setPortFlow 3
setPortModem 3
modemDevice phone
if \goodres\ <> 1 then goto vege
modemReset
if \goodres\ <> 1 then goto vege

echo "\time\ - ready, press * to get help..."

menu:
ClearDetect
ModemReadLn 60 1
if \readLen\ < 1 then goto menu
if \readEd\ = * then goto Help
if \readEd\ = # then goto start
if \readEd\ = 0 then goto Vege
if \readEd\ = 1 then goto Key1
if \readEd\ = 2 then goto Key2
if \readEd\ = 3 then goto Key3
if \readEd\ = 4 then goto Key4
if \readEd\ = 5 then goto Key5
if \readEd\ = 6 then goto Key6
if \readEd\ = 7 then goto Key7
if \readEd\ = 8 then goto Key8
if \readEd\ = 9 then goto Key9
echo "invalid key entered, press * to get help!"
goto menu

help:
echo "* - this help..."
echo "# - reinitiailize modem..."
echo "0 - leave this code..."
echo "1 - play from current file..."
echo "2 - display current filename..."
echo "3 - record to current file..."
echo "4 - find previous file..."
echo "5 - generate unique filename to current..."
echo "6 - find next file..."
echo "7 - delete current file..."
echo "8 - delete & record current file..."
echo "9 - record to a new file..."
goto menu

key1:
echo "playing from \workPath\\currFile\... press * to abort..."
ModemPlay \workPath\\currFile\ *#
echo "done..."
goto menu

key2:
echo "Current File: \workPath\\currFile\..."
goto menu

key3:
echo recording to \workPath\\currFile\... press * to abort...
ModemRecord \workPath\\currFile\ 50000 *#
echo "done..."
goto menu

key4:
SetCurrentFile \prevFile\
goto key2

key5:
SetCurrentFile \newFile\
goto key2

key6:
SetCurrentFile \nextFile\
goto key2

key7:
echo "erasing \workPath\\currFile\..."
eraseFile \workPath\\currFile\
SetCurrentFile ""
goto menu

key8:
echo "erasing \workPath\\currFile\..."
eraseFile \workPath\\currFile\
goto key3

key9:
SetCurrentFile \newFile\
goto key3

vege:
echo "deinitializing..."
modemHangup 1
modemCommand atz
quit
