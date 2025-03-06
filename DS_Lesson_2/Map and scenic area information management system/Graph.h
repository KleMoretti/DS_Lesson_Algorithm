#pragma once
#include<iostream>
#define MAX_VERTEX_NUM 20
#define MAX_NAME_LEN 20
#define MAX_DESC_LEN 1024

struct Vex {
	int num;
	char name[MAX_NAME_LEN];//景点名字
	char desc[MAX_DESC_LEN];//景点介绍
};

struct Edge {
	int vex1;
	int vex2;
	int weight;
};
struct PathList {

};

class CGraph {
public:
	CGraph(void);
	~CGraph(void);
private:
	int m_aAdjMatrix[20][20]; //邻接矩阵
	Vex m_aVexs[20]; //顶点数组
	int m_nVexNum;
public:
	void Init();
	bool InsertVex(Vex sVex);
	bool InsertEdge(Edge sEdge);
	Vex GetVex(int nVEx);
	int FindEdge(int nVex, Edge aEdge[]);
	int GetVexNum();
	void DFSTraverse(int nVex, PathList &List);
	int FindShortPath(int nVexStart, int nVexEnd, Edge aPath[]);
	void FindMinTree(Edge aPath[]);
	void DFS(int nVex, bool aVisited[], int& nIndex, PathList& List);
};