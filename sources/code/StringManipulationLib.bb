;String Library Module
;--------------------------------------------
;
;Jim Pishlo
;CoderLaureate@BlueFireStudios.com
;www.BlueFireStudios.com
;

;Global Definitions for some of these functions.
Const ITEMS_ARRAY_SIZE = 500


;newString$ = common$(string$)
;----------------------------------------------------
;returns a lowercase, trimmed version of string for
;comparison to other strings where case does not matter.
;
Function common$(s$)
     Return Lower$(Trim$(s))
End Function

;charCount% = count_chars(string$,char$)
;----------------------------------------------------
;returns the number of occurances of the char, or char
;sequence of char$ in string$.
;This is case sensitive.
Function count_chars(s$,c$)
     cnt = 0          ;initialize count
     pos = 1          ;first char. position in string
     Repeat
          n = Instr(s$,c$,pos)
          If n = 0 
               Exit
          Else
               cnt = cnt + 1
               pos = n + 1
          End If
     Forever
     Return cnt
End Function

;split(string$, delimiter$, blitz_array$)
;----------------------------------------------------
;splits a string of characters up into the blitz_array$[] 
;you specify.  spliting is based upon the delimiter$ you
;specifiy.  I.E., split "a,b,c", "," will split everything
;separated by a comma.
;returns number of items retrieved.
Function split(s$,d$,arr$[ITEMS_ARRAY_SIZE])
     cnt = count_chars(s$,d$)      ;get number of delimters in string
     itm = 0                              ;first array element
     pos = 1                              ;first position in string
     If cnt <> 0
          Repeat
               n = Instr(s$ + d$,d$,pos)     ;look for delimiter
               If n = 0          
                    Exit                         ;exit if none found
               Else
                    i$ = Mid$(s$,pos, n - pos)     ;get chars between new delimiter and old
                    arr[itm] = Trim$(i$)          ;store into blitz_array
                    itm = itm + 1
                    pos = n + 1                    ;point to char after new delimiter
               End If
          Forever
     Else
          arr[itm] = s$                    ;if no delimiters found return whole string
          cnt = -1
     End If
     Return cnt + 1
End Function

;new_string$ = build$(itemcount%, delimiter$, blitz_array$[])
;--------------------------------------------------------------------
;recombines a recently split blitz_array$[] array back into a
;string using the delimiter you specify.
;returns new string.
Function build$(ic%,d$, arr$[ITEMS_ARRAY_SIZE])
     s$ = ""
     c$ = ""
     For i = 0 To ic - 1
          s = s + c$ + arr[i]
          If c$ = "" Then c$ = d$
     Next
     Return s$
End Function

;new_string$ = insert_text$(string$, text$, position)
;-----------------------------------------------------------
;inserts text$ into string$ starting one char past position.
;returns new string.
Function insert_text$(s$,t$,p)
     l$ = Left$(s$,p)
     r$ = Mid$(s$,p+1)
     Return l$ + t$ + r$
End Function

;new_string$ = over_write$(string$, text$, position)
;----------------------------------------------------------
;over writes text in string$ with new text$ starting at
;one char after position.
;returns new string.
Function over_write$(s$,t$,p)
     l$ = Left$(s$,p)
     r$ = Mid$(s$,(p+1) + Len(t$))
     Return l$ + t$ + r$
End Function

;new_string$ = get_data$(string$, d1$, d2$, position)
;----------------------------------------------------------
;gets the data located in between delimiters d1$ and d2$
;starting at position.
Function get_data$(s$,d1$,d2$,pos)
     n = Instr(s$,d1$,pos)
     d$ = ""
     If n <> 0
          pos = n + 1
          n2 = Instr(s$,d2$,pos)
          If n2 <> 0 
               d$ = Mid$(s$,pos,(n2 - pos))
          End If
     End If
     Return Trim$(d$)
End Function

;new_string$ = get_descriptor$(string$, d1$, position)
;---------------------------------------------------------
;gets the text in a command string that leads up to the first
;delimiter.
Function get_descriptor$(s$, d1$, pos)
     n = Instr(s$,d1$,pos)
     d$ = ""
     If n <> 0
          d$ = Left$(s$,n-1)
     End If
     Return Trim$(d$)
End Function