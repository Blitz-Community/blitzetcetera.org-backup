;Функции UUE-подобного кодирования / декодирования
;Позволяют встраивать файлы в программу с помощью операторов Data
;Используйте процедуру декодирования, затем поместите строки "Data" из
; файла в тело программы, поставив перед ними метку. Когда нужно будет загрузить
; файл - используйте команду "Restore 'метка'" и запускайте порцедуру Decode.
; Загружайте полученый файл "data.tmp" соотв командой, затем сотрите его.
;Соотношение: 3 байта (0-255) = 4 6-битовым кускам (48 - 112)

;encode "sound.mp3","sound.dat":End

Restore sound_mp3: decode
snd=LoadSound("data.tmp")
chn=PlaySound(snd)
While ChannelPlaying(chn):Wend
FreeSound snd
DeleteFile "data.tmp"

Function encode(infile$,outfile$)
l=FileSize(infile$)
inf=ReadFile(infile$)
outf=WriteFile(outfile$)
WriteLine outf,"Data "+l
For n=1 To l Step 3
 a1=ReadByte(inf)
 m$=m$+Chr$((a1 And %111111)+48)
 If Not Eof(inf) Then a2=ReadByte(inf)
 m$=m$+Chr$(a1 Shr 6+(a2 And %1111) Shl 2+48)
 If Not Eof(inf) Then a3=ReadByte(inf)
 m$=m$+Chr$(a2 Shr 4+(a3 And %11) Shl 4+48)
 m$=m$+Chr$(a3 Shr 2+48)
 q=(q+4) Mod 72
 If q=0 Or (Abs(l-n)<3) Then
  WriteLine outf,"Data "+Chr$(34)+m$+Chr$(34)
  m$=""
 End If
Next
CloseFile inf
CloseFile outf
End Function

Function decode()
outf=WriteFile("data.tmp")
Read q,im$
n=1
Repeat
 a=Asc(Mid$(im$,n+1,1))-48
 WriteByte outf,Asc(Mid$(im$,n,1))-48+(a And %11) Shl 6
 If q=1 Then Exit
 b=Asc(Mid$(im$,n+2,1))-48
 WriteByte outf,a Shr 2+(b And %1111) Shl 4
 If q=2 Then Exit
 WriteByte outf,(b Shr 4)+(Asc(Mid$(im$,n+3,1))-48) Shl 2
 q=q-3
 If q=0 Then Exit
 n=n+4
 If n=73 Then n=1:Read im$
Forever
CloseFile outf
End Function

.sound_mp3
Data 3744
Data "o??@43`41VTQiT4B30B]YL682LH4SjFQ18h@8Q04P0<^\9^=Y;03L:HPfRL00@\@QKKQ]Q::"
Data "<Xb53lf8^E819Zh`_eU^3^PR5[GSe2A<XE<ITSYA[LBT04ZR04TUa@>07<^e5f:=n=joc34a"
Data "=PQ`>0J=?RV8XD=KoaHMP1I1??aPR0XG2i:C`OKRHeUPhkelTeDc=TdM;QnV?64j:U@PZP7^"
Data "<iOOYaL34`\^C5DIb0DQoG@VPLS4<257V=>dL3W8J1U>EncoRb?^IoJI02@0:ool0AL1G4TR"
Data "QGiSQ90;c300Hbi83oh1]00LjL\R<J_^hAOALB8?\WP[oHM91AgM`<G2^]@aAaoCjF2[NeKb"
Data ";cJDcdWk__IcV8eMf`j`_Vm@d_g<clk;jBURg?klWOH42lnXX8g@]38Po??@4?P50J_h_l8N"
Data "000P01a8g3@2h4=HIdRaIhZNN\Ta3QElZ<DCdNT;jl]:9BNbCW<=lZ=J0eDF@eFH3`iG_g_n"
Data "^o5OCSW]UHMHTST77?fd04k0`QHLek@KoL9o9]_kkoWRo7\?5djoc34a4DA0>QmY?S50NL]Q"
Data "?amLc;2HIF:GlEQflXcJLENRd<PdEDJh`WV1;kTk5fe6]C0GCJK<^G>d7[abHdIWWf=O?EL?"
Data "gAY<KliY:3KLef9YEJ;^7ZimnE:8Zo?X0<`9hR@AaT;3?nol0Al2D4XDQKoB6`jG62:AGgN2"
Data "0Tn=HVc=fU9Q2V:0Zd0acD76dOgJH1ES<XBSoY[U^B1oc551K7i^U>B4@:@:XkMTSi4?:CY:"
Data "eEWRhiC65IUOO2H0IYT08974ChCocFi=<ZJDRFJPo??@4C151JEiFQDa><nT:B3VQ@`PPP<J"
Data "P`gVmX1=b\:e_<^H3nPR=<J<KGJF3BEJ]CZBhj9Q;PPK29VU0UlJU:dRaMQmjiL0MYf?EF@V"
Data ":5@>UVlQ7^IV<`445;ODd:4a:QP5Ji>AS;foc34aM@A`BE]E8MT9@V<>FTV4Z[gfU;?=H]YZ"
Data "HEeQBVhN?IjJ85T]Uh@DK\26<UN]\05^EjbOWWH8A_QaNcj7@:m@i3@e>1E8cPZ7B2YcnTiU"
Data "TDTaf\l8DPc1QPCS1L>M2AK1GDb8<lol0AL9D4\:UKWN6I50eh?OH5EFhdQjkE<O9ElN@C0V"
Data "0Q8lJ=n`C\d^4YWDdP1J4QPUcV<Tn:bo@8eC@;@Vn90bBYWhBT6[VenZJD_XX4`0kk>3C4\k"
Data "R9;\<>\k\nB8YFKmSd@iDlJmo??@4g251kBfF]Tall;J^3If=SD\785@RHR^KGIHj0Ii<]ie"
Data "W2M@;R=ehDe;Q9W8^6g3P7@Vj0PUj_>\BQ1mn;e^o47;?UXfMJiaW_1Ro5JHLn:XRnh4U9_Y"
Data "EV42fiNL76Z9ii8LHdeoc34ae@A`nF^ESY\h^=8BfUfOloL[HIMRPTY<2e8?MjG\JlWl2J@7"
Data "fDo[0>`]0IfN>X0ZcZf[[FdQWMZ::C>8ZQd:kTHBT0M]9Q>kbEdFgE?P>aCHSDhTRCcPnA1@"
Data "kT3E5AIcGab::lol0AL?D4\8Qk4H5YB7:;HX@:1PP;]aiN<4:Tm^gci30F`_`>RDhjPl1ODE"
Data "JTDC`VTLC0:TX9TBD9Ph=;BK8D01<A3AZ<GD:B9d^Z\GSMakaT7]D`::6[N3<B_ON7e?lPDA"
Data "BZbYmn7Xo??@4GD51h8fF;3AB2eT8AJ\U<PDHC9DHW8IoY?PMTL9LG069780<UB<@bDOT20k"
Data "P950Y;I^m1Y8QM\@\H7ABD62n>io<TlZ^Sf8:4k=lUe<G]eF:]gRZeYjUf72:P6OGooon\U:"
Data "FGnoc34a<EALR4^W9IhJdoc]oONII>I6lFX80Tb8c1V8jD99FX@M40H<\bLD4BL<6XbZB\WJ"
Data "c@2oM=omFHhbKRQ@Z3104DX7QR;YiO8@Ej\^?m\nL0n?T;RN5AA2GYo19@1h3cMd1FVV3lol"
Data "0ALDE4XSVkSB6X3V>4SYoP\2E[L0NAR]RcAG>Pfloi<\iJeE\ieGhgfC7EN?8@B:eICCoO?4"
Data "K>:EKcK9ZM_4\8PhTgmgjf84=^a3:HNY3iWYiN41Tj<0V682oMI0j8UD?lAC05B@o??@4KU5"
Data "1HEhF=6d6CeO46@V:50NEki`DX67FklX\ChBFl=jD2H>glfC9TUR4;_4Q]c=7G]oDVNi`W;U"
Data "<gOk<PX2>JNEhTP8Y6Z2eT@i638@YT:h_MW:PbGR@^BZ0j]jMIj75JDF8?`oc34aIEA0>5^E"
Data "SI\bDC8eCLSCAX;=IKKJ7980H605GZ51QFV1574Lf90;KR_<76RhP<X2B5_4R23LU408<0Ul"
Data ">601^A_GX2kXNJmRd<\DDaHfffa390L]iD3@^ZmWI50e^3^;e2ddFmol0A<HE4X4UKEB7:0@"
Data "ikb>BeU@Q918U>P3[j899B@YP<JFI;Oo:D67c2:;7kP3Zj=c=OeoP3j8P>]nU8iX=o@Wo5Fj"
Data "kNLNP=nTJLZb3g:;6T@04LTEo0i=kC;2YFh8o\<ZeYWE:D>Mo??@4GF51HDhfUTQ^B8H@:KU"
Data "I1[=2:;162Ii?PaTGXnWcX:Ka93Yk]Jh<<J1<jl_kjI:n=1CjCEac05][SU;26ZY@I?[HUhG"
Data "l^^0=B3XhVLIff7MWRX0S?9HM\b]\L8jYh5Q8aC]KEaoc34a\IA@B2\eQIXb=HI[iPdB=bma"
Data "jW^JkT\Comfo?U`Ik5A=?eRkMhf2T@P\nk:Tm`_oAS^1nIQ;Lohogn_oFCWeRQC`8GYEDSGP"
Data "^K=o87`8C2f6DY9M@6mddSV[o^Wg:LA_f_R3@nol0A\KE4SA=k5H=nPGn6>gG5377\SNomoc"
Data "9fmmg>K[JZFaDWCPLPgNYg=jnaG?kMIC04VS\>Sooi_OD`GKlng?HCf^LmEVgf?P0BAK=3Xd"
Data "KnUYJdS?N@cojf1bZKElGdT66kM45nhXo??@4CG51IDfN5FC;P:2;klgF1Mm^AiJ?Jf@Nm9d"
Data "lm^9nl_Qnh^f?E16WL10Wl:eSReUP_JaDl@Z?jPP0264f=ocA<d_12`;i3K242d[VELYNPKV"
Data "[b:Kgc8M^Lf:0FHgF316VHYGbogoc34ajEA`Ze]oheX2@7R3<OGU\X5eGNe17iV[;?FY>`<S"
Data "meMOm:VfWgnWoffISh2Q2U5X8[oPMdQ8Z28?8BlBmc]0TOjTdi@jEKIW^d@L>EQ1R<SfJOSM"
Data "]kHIWG[Ng_ndkc>emPJLnool0A\ODX^QQK?B5<2;bCo`cS0IfimanJYW\`MJDa19X<;eeLTB"
Data "Y4Na\mo<_@1Ef3Tl67i4GQ81LO7LF<eO=cAWDcPV?:Dme5KUSU][f]^A>GGGOW^USio=ZZZb"
Data "EQQkIl7eYT2UD0Kjo??@4G851KEiF1F3RDU;AfMOI6nooie5m>2SoNKSTO7iIOEC;TI7_\m5"
Data "LT6g]P1JP3P2;E6o5@h]A0HJ6@5YlU4Re^<:ZKF5o@R:flN@5i_0HA49YkbI?AkCJ_jPCX<Y"
Data "Ch0=JXiKLOfoc34a=FQ:N4^GPM`88LjBT]6LONR1UKI]hooo2:k_[]9^MkOm<h?Zob>T0L9;"
Data "F2XW47I6Vfk46lmDfTS;:8FlAbC<NPD_WJl9ZEmnWfTdUhYNh``cok?7cMh[]5nJkDEM4UQP"
Data "6QDThlol0AlTE4SDEk9B7M0dU=FbA:0:CQFHS9jOlY^G;0H<S]>=2F?=bZ:S00@Nf]9H:PlA"
Data "`7cJLSZRmnf_NPdK:hcVXQ1FCSojJe_4Uj;NhSDnKH2AOY]G]Y7?NXHDFn@\[TW5=^cUS:cK"
Data "o??@4WY51YE_n]W16k?kF@S724CP:S=mkRQeJeCHabQI0N2d6@^LPk@U25a;RfIfLSJNA`kd"
Data "c=228g:;<jP47gW1^9[nHYm1?mo\Vlnc\?Xki\WWKYc6@Ao_H2B8_H=h2CEL_:T46^noc34a"
Data "KFAPV;]?RYPf116nom3neJT4Wc:K==`Q2XT6TA33o1k1T<7HE4CIhhXPLgWDN;k5BAlnoPDJ"
Data "W\g5Ni83EaGnj[i:UTdoaJhSe?cZ\T26DV2]RZQkH@PFW6Af;dm5DMjdT;W;fnol0A<XF4\G"
Data "5k5N=J0mo?[Wo9T4GV`H7J8ncSgoHGfD0Zd\TK;:8Wi\KBXB]Y]0PJcCSKn^RU6K[9aV<[TJ"
Data ";Ii<7dhfleYm;@R9?;d^]h3R4\2e9aEJ>;C[5@Wo0nDlg\fQ>UJ^LAE5o??@43J5Z9HeNUGA"
Data "?40gfmk:6D:W2lhe9IM[]FA`kb1]1`2WjJ2S>blINOlhTh0`hX<0ARXfe>Z`Xg@7M]nLIl_k"
Data "WK6j4T6SK]ah5X:GU5T1IAWB1Ne6;o7igm?oh^9B`bjT=loIW4ooc34aUJAP^QZ51GX0o2:V"
Data "[dYb?DG[^_6G1<ITi2P5AllK[eCE_:SbE:eKST\Jea5>;9GeQiTL\J;]Fm;`:;`DDh_k44RZ"
Data "Yj?c77>R>oQHFo>N:g\n`JZ13F:A1=2`EkJl?[]2kfQ4Knol0A\YE4\ITj;H7nPRE4EF@IU;"
Data "DiF\Q2jiQ4X:7g6[0@1PeFkLWKR5J5>mZR=S?OEA93X@AZ^Vd`HTB0hDS7E`29YbIOdbA2VZ"
Data "l81SKAH`I3eeQG]3=P`STddQ1aU>U\CTBGBihIYdo??@4[j51h6NN=VabE`Za1f;4o50L@e`"
Data "Q@DaghESkVl5K1:]BA3hbXL;L>:cgNFZh8mR\\JS:KiI7NVXZnWo:nkoR:j:kPQYE=d\aZ8R"
Data "XnkooOoIY8XP116LCS?ZW2=JZP29ooooooooc34aYF1@:GTWka<5oooooooooooooooooooo"
Data "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
Data "oooooooooooooooooooooooooooooool0Al[C8LA\1@`4beooooooooooooooooooooooooo"
Data "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
Data "oooooooooooooooooooooooo"