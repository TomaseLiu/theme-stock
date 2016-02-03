package com.datayes.bdb.theme.stock.business;

//import com.datayes.bdb.theme.helper.constant.DatasourceConst;
import com.datayes.search.client.es.EsSearcher;
import com.datayes.bdb.theme.stock.constant.ConfigConst;

public class BaseSearcher {
	static String[] hosts = ConfigConst.SEARCH_URL_LIST;
	static String clusterName = ConfigConst.CLUSTER_NAME;
	protected static EsSearcher client = new EsSearcher(hosts, clusterName);
}
