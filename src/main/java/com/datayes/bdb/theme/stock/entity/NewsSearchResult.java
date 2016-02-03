package com.datayes.bdb.theme.stock.entity;

import java.util.List;
import com.datayes.bdb.theme.stock.entity.NewsElasticSearch;

public class NewsSearchResult {
	private List<NewsElasticSearch> list;
	private int pageIndex;
	private int pageSize;
	private long total;

	public NewsSearchResult() {
	}

	public NewsSearchResult(List<NewsElasticSearch> list, int pageIndex,
			int pageSize, long total) {
		this.list = list;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.total = total;
	}

	public List<NewsElasticSearch> getList() {
		return list;
	}

	public void setList(List<NewsElasticSearch> list) {
		this.list = list;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
