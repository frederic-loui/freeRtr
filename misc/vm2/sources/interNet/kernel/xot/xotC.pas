{$heap 63k}
{$stack 3k}
{$sysinc system.inc}
{$sysinc param.inc}
{$sysinc hex.inc}
{$sysinc bugos.inc}
{$sysinc pipeline.inc}

{$sysinc memory.inc}
{$sysinc inet_addr.inc}
{$sysinc inet_tcp.inc}

{$include xot.inc}
{$include xotC.inc}

Var i,o:LongInt;
BEGIN;
WriteLn('xot client v1.0, done by Mc at '#%date' '#%time'.');
if TCPfindProcess then immErr('failed to find tcp process!');

if string2ipAddr(ParamStr(1),prAdr) then immErr('using: xot.code <host> [port]');
prPrt:=BVal(ParamStr(2));
if (prPrt=0) then prPrt:=1998;

Write('connecting to '+ipAddr2string(prAdr)+' '+BStr(prPrt)+'...');
TCPbeginConnect(tcpPipe,65536,prAdr,prPrt);
while TCPlookConnected(tcpPipe,myAdr,myPrt,i) do begin;
  if (tcpPipe=0) then immErr(' failed!');
  relequish;
  end;
WriteLn(' ok!');
WriteLn('local side is '+ipAddr2string(myAdr)+' '+BStr(myPrt)+'...');

WaitForUpperLayer;
doOneConnection;
END.