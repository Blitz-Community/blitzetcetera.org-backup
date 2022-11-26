//andreyman.ucoz.ru
//(c)ANDREYman



#include "stdafx.h"

#define BBDECL extern "C" _declspec(dllexport)
#define BBCALL _stdcall


typedef struct _BBVERTEX
{
     float x,y,z;
     float nx,ny,nz;
     char a,r,g,b;
     float u0,v0;
     float u1,v1;
     char i1,i2,i3,i4;
     float w1,w2,w3,w4;

} BBVERTEX, *LPBBVERTEX;


typedef struct _BBTRIANGE
{
         unsigned short a,b,c;

} BBTRIANGE, *LPBBTRIANGE;






BBDECL void TransformSurfaceToMatrix( unsigned int *surfaceSrc, unsigned int *surfaceDest, float *Entity )

{
LPBBVERTEX SrcVertexBuffer, DestVertexBuffer ;

float x,y,z;

//Глобальная матрица:
float	EMatrix11= *(Entity+44);
float	EMatrix12= *(Entity+45);
float	EMatrix13= *(Entity+46);

float	EMatrix21= *(Entity+47);
float	EMatrix22= *(Entity+48);
float	EMatrix23= *(Entity+49);

float	EMatrix31= *(Entity+50);
float	EMatrix32= *(Entity+51);
float	EMatrix33= *(Entity+52);

float	EMatrix41= *(Entity+53);
float	EMatrix42= *(Entity+54);
float	EMatrix43= *(Entity+55);

/*//Локальная матрица:
float	EMatrix11= *(Entity+22);
float	EMatrix12= *(Entity+23);
float	EMatrix13= *(Entity+24);

float	EMatrix21= *(Entity+25);
float	EMatrix22= *(Entity+26);
float	EMatrix23= *(Entity+27);

float	EMatrix31= *(Entity+28);
float	EMatrix32= *(Entity+29);
float	EMatrix33= *(Entity+30);

float	EMatrix41= *(Entity+31);
float	EMatrix42= *(Entity+32);
float	EMatrix43= *(Entity+33);*/


SrcVertexBuffer	 = (struct _BBVERTEX *)*(surfaceSrc+7); //Адрес начала вершинного буфера исходной сетки
DestVertexBuffer = (struct _BBVERTEX *)*(surfaceDest+7); //Адрес начала вершинного буфера трансформируемой сетки

*(surfaceDest+16)=0; //Обнуление этого флага означает изменение вертекс буфера, после чего он будет передан видеокарте

int VertexCount = ( *(surfaceSrc+8) - *(surfaceSrc+7) )/64;//(VertBufEnd-VertBufStart)/64; //Количество вершин в сетке

for (int vertex = 0; vertex<VertexCount; vertex++)
  {
	x=SrcVertexBuffer[vertex].x;
	y=SrcVertexBuffer[vertex].y;
	z=SrcVertexBuffer[vertex].z;

 // Здесь мы производим перемножение координат вершины исходной сетки на матрицу и запись результата в изменяемую сетку
	DestVertexBuffer [vertex].x = EMatrix11*x + EMatrix21*y + EMatrix31*z + EMatrix41; 
	DestVertexBuffer [vertex].y = EMatrix12*x + EMatrix22*y + EMatrix32*z + EMatrix42;
	DestVertexBuffer [vertex].z = EMatrix13*x + EMatrix23*y + EMatrix33*z + EMatrix43;	
  }

}
