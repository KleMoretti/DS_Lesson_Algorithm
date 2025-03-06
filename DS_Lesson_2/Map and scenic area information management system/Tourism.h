#pragma once
#include"Graph.h"
class CTourism {
private :
	CGraph m_Graph;
public:
	CTourism(void);
	~CTourism(void);
	void CreateGraph();
	void GetSpotInfo();
};