#include "Graph.h"

CGraph::CGraph(void)
{
}

CGraph::~CGraph(void)
{
}

void CGraph::Init()
{
	m_nVexNum = 0;
	for (int i = 0; i < MAX_VERTEX_NUM; i++) {
		for (int j = 0; j < MAX_VERTEX_NUM; j++) {
			m_aAdjMatrix[i][j] = 0;
		}
	}
}

bool CGraph::InsertVex(Vex sVex)
{
	if (m_nVexNum == MAX_VERTEX_NUM) {
		return false;
	}

	m_aVexs[m_nVexNum++] = sVex;
	return true;
}

bool CGraph::InsertEdge(Edge sEdge)
{
	if (sEdge.vex1 < 0 || sEdge.vex1 >= m_nVexNum || sEdge.vex2 < 0 || sEdge.vex2 >= m_nVexNum) {
		return false;
	}

	m_aAdjMatrix[sEdge.vex1][sEdge.vex2] = sEdge.weight;
	m_aAdjMatrix[sEdge.vex2][sEdge.vex1] = sEdge.weight;
	return true;
}

Vex CGraph::GetVex(int nVEx)
{
	return m_aVexs[nVEx];
}

int CGraph::FindEdge(int nVex, Edge aEdge[])
{
	int k = 0;
	for (int i = 0; i < m_nVexNum; i++) {
		if (m_aAdjMatrix[nVex][i] != 0) {
			aEdge[k].vex1 = nVex;
			aEdge[k].vex2 = i;
			aEdge[k].weight = m_aAdjMatrix[nVex][i];
			k++;
		}
	}
	return k;
}


int CGraph::GetVexNum()
{
	return m_nVexNum;
}

void CGraph::DFSTraverse(int nVex, PathList& pList)
{
	int nIndex = 0;
	bool aVisted[MAX_VERTEX_NUM] = { false };
	DFS(nVex, aVisted, nIndex, pList);
}

int CGraph::FindShortPath(int nVexStart, int nVexEnd, Edge aPath[])
{
	return 0;
}

void CGraph::FindMinTree(Edge aPath[])
{
}

void CGraph::DFS(int nVex, bool aVisited[], int& nIndex, PathList& pList)
{
	aVisited[nVex] = true; // 已经访问过
	pList->vexs[nIndex++] = nVex;

	int nVexNum = 0;
	for (int i = 0; i < m_nVexNum; i++) // 搜索 nVex 的所有邻接点
	{
		if (aVisited[i])
		{
			nVexNum++;
		}
	}
	if (nVexNum == m_nVexNum) {
		pList->next = (PathList)malloc(sizeof(Path));
		for (int i = 0; i < m_nVexNum; i++) {
			pList->next->vexs[i] = pList->vexs[i];
		}
		pList = pList->next;
		pList->next = NULL;
	}
	else {
		for (int i = 0; i < m_nVexNum; i++) {
			if ((!aVisited[i]) && (m_aAdjMatrix[nVex][i] > 0)) {
				DFS(i, aVisited, nIndex, pList);

				aVisited[i] = false;
				nIndex--;
			}
		}//for
	}
}

