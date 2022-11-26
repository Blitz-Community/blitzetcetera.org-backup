;****************************************
;Name: Join/Split Banks
;Author: A Pissed Off Boy
;Date: 7:32am , 4/22/04
;Updated: n/a
;****************************************

;Purpose: Lets you Merge 2 Banks into a single bank, or split a bank into 2 seperate banks.
 
Function JoinBanks(Bank1%,Bank2%,FreeSourceBanks%=True)
     Local Size1%=BankSize(Bank1%),Size2%=BankSize(Bank2%),OutBank%=CreateBank(Size1%+Size2%)
     CopyBank Bank1%,0,OutBank%,0,Size1%
     CopyBank Bank2%,0,OutBank%,Size1%,Size2%
     If FreeSourceBanks%=True
          FreeBank Bank1%
          FreeBank Bank2%
     EndIf
     Return OutBank%
End Function

Function SplitBank(Bank%,Offset%)
     Local InSize%=BankSize(Bank%),OutSize%=InSize%-Offset%,OutBank%=CreateBank(OutSize%)
     CopyBank Bank%,Offset%,OutBank%,0,OutSize%
     ResizeBank Bank%,InSize%-OutSize%
     Return OutBank%
End Function