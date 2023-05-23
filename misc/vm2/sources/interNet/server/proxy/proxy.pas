{$heap 63k}
{$stack 3k}
{$sysinc system.inc}
{$sysinc alap.inc}
{$sysinc hex.inc}
{$sysinc filesys.inc}
{$sysinc textfile.inc}
{$sysinc param.inc}
{$sysinc bugos.inc}
{$sysinc pipeline.inc}

{$sysinc memory.inc}
{$sysinc inet_addr.inc}
{$sysinc inet_tcp.inc}
{$sysinc inet_dns.inc}
{$include \sources\internet\kernel\utils\timer2.inc}

Const proggyName='proxy server v1.0';

{$include memory.inc}
{$include config.inc}
{$include proxy1.inc}
{$include proxy2.inc}




Label f1;
Var
  con:OneConnectionRecord;
  i,o,p:LongInt;
  a,b:String;
BEGIN;
WriteLn(proggyName+', done by Mc at '#%date' '#%time'.');
SSHfindProcess;
processSSH:=TCPprocessId;
TLSfindProcess;
processTLS:=TCPprocessId;
if TCPfindProcess then immErr('failed to find tcp process!');
processTCP:=TCPprocessId;
if DNSstartResolver then immErr('failed to find dns process!');

ConnectionNum:=0;
lastSent:=0;
if pipeLineBegListen then immErr('failed to start listening!');

a:=paramStr(1);
if (a='') then immErr('using: proxy.code <config>');
ReadUpConfig(a);

if (serverPort=0) then serverPort:=1080;
i:=serverPort;
if TCPlistenOnPort(p,65536,serverAddr,i) then immErr('failed to listen on port!');
serverPort:=i;
WriteLn('listening on '+ipAddr2string(serverAddr)+' '+BStr(serverPort)+' port...');

BugOS_SignDaemoning;
f1:
relequish;
timer2start;
while (pipeLineGetIncoming(p)=0) do begin;
  if ResizeMem(ConnectionNum+1) then begin;
    pipeLineClose(p);
    goto f1;
    end;
  fillchar(con,sizeof(con),0);
  con.pipe1:=p;
  con.time:=CurrentTime;
  con.stat:=1;
  ConnectionDat^[ConnectionNum]:=con;
  end;
repeat
  i:=DNSresolveGet(b,a);
  updateResolvers(i,b,a);
  until (i=0);
for i:=ConnectionNum downto 1 do if doConn(ConnectionDat^[i],i) then begin;
  con:=ConnectionDat^[i];
  pipeLineClose(con.pipe1);
  pipeLineClose(con.pipe2);
  pipeLineClose(con.pipe3);
  move(ConnectionDat^[ConnectionNum],ConnectionDat^[i],sizeof(con));
  ResizeMem(ConnectionNum-1);
  end;

goto f1;
END.