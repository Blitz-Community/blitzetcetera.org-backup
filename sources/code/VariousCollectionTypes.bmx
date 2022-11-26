SuperStrict

Public

' Thanks to Robert Jenkins for the original function
' Read his paper http://burtleburtle.net/bob/hash/evahash.html
Function HashKey%( s$ ) NoDebug ' We really don't want to debug this...
    Local a%,b%,c%,l%,p@ Ptr,k@ Ptr
    
    p = s.ToCString( )
    k = p
    l = s.Length
    a = $9e3779b9
    b = a
    c = 52867
    
    While l >= 12
        a :+ k[0] + (k[1] Shl 8) + (k[2] Shl 16) + (k[3] Shl 24)
        b :+ k[4] + (k[5] Shl 8) + (k[6] Shl 16) + (k[7] Shl 24)
        c :+ k[8] + (k[9] Shl 8) + (k[10] Shl 16) + (k[11] Shl 24)
        ' mix
        a=a-b;  a=a-c;  a=a ~ (c Shr 13)
        b=b-c;  b=b-a;  b=b ~ (a Shl 8)
        c=c-a;  c=c-b;  c=c ~ (b Shr 13)
        a=a-b;  a=a-c;  a=a ~ (c Shr 12)
        b=b-c;  b=b-a;  b=b ~ (a Shl 16)
        c=c-a;  c=c-b;  c=c ~ (b Shr 5)
        a=a-b;  a=a-c;  a=a ~ (c Shr 3)
        b=b-c;  b=b-a;  b=b ~ (a Shl 10)
        c=c-a;  c=c-b;  c=c ~ (b Shr 15)
        k :+ 12
        l :- 12
    Wend
    
    c :+ s.Length
    
    Select l
        Case 11 c=c+(k[10] Shl 24)
        Case 10 c=c+(k[9] Shl 16)
        Case 9  c=c+(k[8] Shl 8) 
        Case 8  b=b+(k[7] Shl 24);
        Case 7  b=b+(k[6] Shl 16);
        Case 6  b=b+(k[5] Shl 8); 
        Case 5  b=b+k[4];              
        Case 4  a=a+(k[3] Shl 24);
        Case 3  a=a+(k[2] Shl 16);
        Case 2  a=a+(k[1] Shl 8)
        Case 1  a=a+k[0];
    End Select
    ' mix
    a=a-b;  a=a-c;  a=a ~ (c Shr 13)
    b=b-c;  b=b-a;  b=b ~ (a Shl 8)
    c=c-a;  c=c-b;  c=c ~ (b Shr 13)
    a=a-b;  a=a-c;  a=a ~ (c Shr 12)
    b=b-c;  b=b-a;  b=b ~ (a Shl 16)
    c=c-a;  c=c-b;  c=c ~ (b Shr 5)
    a=a-b;  a=a-c;  a=a ~ (c Shr 3)
    b=b-c;  b=b-a;  b=b ~ (a Shl 10)
    c=c-a;  c=c-b;  c=c ~ (b Shr 15)
    
    MemFree( p )
    
    Return c
End Function

Type IListIter
    Field l:ILink
    
    Method HasNext%( )
        Return l<>Null
    End Method
    
    Method NextObject:Object( )
        Local v:Object = l.v
        l = l.NextLink( )
        Return v
    End Method
End Type

Type ILink
    Field n:ILink
    Field p:ILink
    Field v:Object
    Field _p:Int Ptr
    
    Method Remove( )
        v = Null
        n.p = p
        p.n = n
        p = Null
        n = Null
        If _p Then _p[0] :- 1
        _p = Null
    End Method
    
    Method Swap( c:ILink )
        Local t:ILink = c.n
        c.n = n
        n = t
        t = c.p
        c.p = p
        p = t
    End Method
    
    Method NextLink:ILink( )
        If n.v <> n Then Return n
        Return Null
    End Method
    
    Method PreviousLink:ILink( )
        If p.v <> p Then Return p
        Return Null
    End Method
    
    Method Value:Object( )
        Return v
    End Method
    
    Method Valid%( )
        If v = Self Then Return False
        Return True
    End Method
    
    Method Compare%( obj:Object )
        Local olink:ILink = ILink(obj)
        If olink Then
            If Not olink.v And Not v Then
                Return 0
            ElseIf Not olink.v Then
                Return 1
            ElseIf Not v Then
                Return -1
            Else
                Return v.Compare( olink.v )
            EndIf
        EndIf
        Return 1
    End Method
End Type

Type IList
    Field c:ILink       ' center link
    Field n:Int = 0     ' link count
    
    Method New( )
        c = New ILink
        c.n = c
        c.p = c
        c.v = c
    End Method
    
    Method Delete( )
        Clear( )
        c.v = Null
        c.n = Null
        c.p = Null
        c = Null
    End Method
    
    Method AddFirst:ILink( obj:Object )
        Local i:ILink = New ILink
        i.v = obj
        AddLinkFirst( i )
        Return i
    End Method
    
    Method AddLast:ILink( obj:Object )
        Local i:ILink = New ILink
        i.v = obj
        AddLinkLast( i )
        Return i
    End Method
    
    Method AddLinkFirst( i:ILink )
        i.p = c.p
        i.n = c
        c.p.n = i
        c.p = i
        i._p = Varptr n
        n :+ 1
    End Method
    
    Method AddLinkLast( i:ILink )
        i.p = c.p
        i.n = c
        c.p.n = i
        c.p = i
        i._p = Varptr n
        n :+ 1
    End Method
    
    Method GetFirst:Object( )
        Local l:ILink = GetFirstLink( )
        If l Then Return l.v
        Return Null
    End Method
    
    Method GetFirstLink:ILink( )
        If c.n = c Then Return Null
        Return c.n
    End Method
    
    Method GetLast:Object( )
        Local l:ILink = GetLastLink( )
        If l Then Return l.v
        Return Null
    End Method
    
    Method GetLastLink:ILink( )
        If c.p = c Then Return Null
        Return c.p
    End Method
    
    Method Remove( obj:Object )
        Local i:ILink = c.n
        Local a% = Int( Varptr obj )
        While i.v <> obj And i <> c
            i = i.n
        Wend
        If i = c Then Return
        i.Remove( )
    End Method
    
    Method Count%( )
        Return n
    End Method
    
    Method ValueAtIndex:Object( idx% )
        Local i:ILink
        Local x:Int
        If idx < n/2 Then      ' Search from the front half of the list
            i = c.n
            While x < idx And i <> c
                i = i.n
                x :+ 1
            Wend
        Else                   ' Search from the back half
            i = c.p
            idx = (n-1)-idx
            While x < idx And i <> c
                i = i.p
                x :+ 1
            Wend
        EndIf
        If i = c Then Return Null
        Return i.v
    End Method
    
    Method Merge( o:IList )
        If Not o Then Return
        Local i:ILink = o.c.n
        If Not i Then Return
        While i <> o.c
            AddLast( i.v )
            i = i.n
        Wend
    End Method
    
    Method Clear( )
        While n
            c.n.Remove( )
        Wend
    End Method
    
    Method ToArray:Object[]( )
        Local arr:Object[n]
        Local l:ILink = c.n
        Local i% = 0
        While l <> c
            arr[i] = l.v
            l = l.n
            i :+ 1
        Wend
        Return arr
    End Method
    
    Method LinkArray:ILink[]( )
        Local arr:ILink[n]
        Local l:ILink = c.n
        Local i% = 0
        While l <> c
            arr[i] = l
            l = l.n
            i :+ 1
        Wend
        Return arr
    End Method
    
    Method Sort( )
        If n < 2 Then Return
        Local arr:ILink[] = LinkArray( )
        arr.Sort( )
        arr[0].p = c
        arr[n-1].n = c
        For Local i:Int = 1 To n-2
            arr[i-1].n = arr[i]
            arr[i].p = arr[i-1]
            arr[i].n = arr[i+1]
            arr[i+1].p = arr[i]
        Next
        c.n = arr[0]
        c.p = arr[n-1]
    End Method
    
    Method Reversed:IList( )
        Local n:IList = New IList
        Local i:ILink = c.n
        While i.Valid( )
            n.AddLast( i.Value( ) )
            i = i.NextLink( )
        Wend
        Return n
    End Method
    
    Method ObjectEnumerator:IListIter( )
        Local i:IListIter = New IListIter
        i.l = c.n
        Return i
    End Method
    
    ' Stack functionality
    Method Push( obj:Object )
        AddLast( obj )
    End Method
    
    Method Pop:Object( )
        If n = 0 Then Return Null
        Local v:Object = GetLast( )
        GetLastLink( ).Remove( )
        Return v
    End Method
    
    Method Peek:Object( )
        Return GetLast( )
    End Method
End Type

Type IHashNode Extends ILink
    Field key:Int
End Type

Type IHashTable
    Field cnt%
    Field buckets:IList[256]
    Field combList:IList
    
    Method New( )
        For Local i:Int = 0 To 255
            buckets[i] = New IList
        Next
    End Method
    
    Method Clear( )
        For Local i:Int = 0 To 255
            buckets[i].Clear( )
        Next
        If combList Then combList.Clear( )
        combList = Null
    End Method
    
    Method Delete( )
        Clear( )
        For Local i:Int = 0 To 255
            buckets[i] = Null
        Next
        buckets = Null
    End Method
    
    Method Retrieve:Object( k:Int )
        Local i:ILink = buckets[k&255].c.n
        Local h:IHashNode
        While i And i.Valid( )
            h = IHashNode(i)
            If h And h.key = K Then
                Return i.v
            EndIf
            i = i.NextLink( )
        Wend
        Return Null
    End Method
    
    Method Insert( o:Object, k:Int )
        Local n:IHashNode = New IHashNode
        n.key = k
        n.v = o
        buckets[k&255].AddLinkFirst(n)
        cnt :+ 1
    End Method
    
    Method Remove( k:Int )
        Local i:ILink = buckets[k&255].c.n
        Local h:IHashNode
        While i.Valid( )
            h = IHashNode(i)
            If h And h.key = K Then
                i.Remove( )
                cnt :- 1
                Return
            EndIf
            i = i.NextLink( )
        Wend
    End Method
    
    Method ObjectEnumerator:IListIter( )
        If combList = Null Then
            combList = New IList
            For Local i:Int = 0 To 255
                combList.Merge( buckets[i] )
            Next
        EndIf
        Local i:IListIter = New IListIter
        i.l = combList.c.n
        Return i
    End Method
End Type

' Expects unique objects -- duplicates should never be used.
' It does not perform checks to see if you add duplicates.
' That would be a waste of time.
Type IObjectTable Extends IHashTable
    Method Get:Object( key:Int )
        Return Retrieve( key )
    End Method
        
    Method Add:Int( obj:Object )
        If combList Then combList.Clear( )
        combList = Null
        Local key% = Int(Varptr obj)
        Insert( obj, key )
        Return key
    End Method
End Type

' Similar to IObjectTable except that it hashes a string
' instead of using an object's memory address as the key
' As such, there can be duplicate objects at the cost of
' having to create a unique key each time.
Type INameTable Extends IHashTable
    Method Add:Int( obj:Object, name$ )
        If combList Then combList.Clear( )
        combList = Null
        Local key% = HashKey( name )
        Insert( obj, key )
        Return key
    End Method
    
    Method GetByKey:Object( key% )
        Return Retrieve( key )
    End Method
    
    Method GetByName:Object( name$ )
        Local key% = HashKey(name)
        Return Retrieve( key )
    End Method
End Type

Type ITreeNode
    Field k%
    Field v:Object
    Field b:ITreeNode ' bottom/lower
    Field t:ITreeNode ' top/upper
    
    Method Search:Object( key% )
        If k = key Then Return v
        If k < key And b Then Return b.Search( key )
        If k > key And t Then Return t.Search( key )
        Return Null
    End Method
    
    Method Add( node:ITreeNode )
        If node = Null Then Return
        If node.k < k Then
            If b Then
                b.Add( node )
            Else
                b = node
            EndIf
        ElseIf node.k > k Then
            If t Then
                t.Add( node )
            Else
                t = node
            EndIf
        Else
            Throw "Collision occurred in tree"
            ' You could throw some code in here to resolve the collision, but I'm not going to.
        EndIf
    End Method
    
    Method Remove%( key% )
        Local r%=0, n:ITreeNode
        If key = k Then
            v = Null
            Return 2
        ElseIf key < k And b Then
            r = b.Remove( key )
            n = b
        ElseIf t
            r = t.Remove( key )
            n = t
        EndIf
        
        If r = 2 And n Then
            Add( n.t )
            Add( n.b )
            n.t = Null
            n.b = Null
            Return 1
        EndIf
        
        Return r
    End Method
End Type

Type IBinaryTree
    Field top:ITreeNode
    Field cnt%
    
    Method Remove( key% )
        If Not top Then Return
        Local r% = top.Remove( key )
        If r = 2 Then
            Local f:ITreeNode = top
            top = f.t
            f.t = Null
            top.Add( f.b )
            f.b = Null
        EndIf
        
        If r > 0 Then cnt :- 1
    End Method
    
    Method Search:Object( key% )
        If top Then Return top.Search( key )
        Return Null
    End Method
    
    Method Insert( node:ITreeNode )
        If top Then
            top.Add( node )
        Else
            top = node
        EndIf
        cnt :+ 1 ' post-collision if one occurs
    End Method
End Type

Type IObjectTree Extends IBinaryTree
    Method Add%( obj:Object )
        Local n:ITreeNode = New ITreeNode
        Local key% = Int( Varptr obj )
        n.k = key
        n.v = obj
        Insert( n )
    End Method
    
    Method RemoveByObject( obj:Object )
        Remove( Int( Varptr obj ) )
    End Method
End Type

Type INameTree Extends IBinaryTree
    Method SearchByName:Object( name$ )
        Return Search( HashKey( name ) )
    End Method
    
    Method Add%( obj:Object, name$ )
        Local n:ITreeNode = New ITreeNode
        Local key% = HashKey( name )
        n.k = key
        n.v = obj
        Insert( n )
    End Method
    
    Method RemoveByName( name$ )
        Remove( HashKey( name ) )
    End Method
End Type