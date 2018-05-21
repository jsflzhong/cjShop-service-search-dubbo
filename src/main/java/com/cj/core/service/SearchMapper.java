package com.cj.core.service;

import org.apache.solr.client.solrj.SolrQuery;

import com.cj.core.pojo.SearchResult;

/**
 * Search服务的dao层
 * 从Solr服务器中,查询搜索结果的mapper.
 * @author 崔健
 * @date 2016年12月16日上午12:20:40
 */
public interface SearchMapper {
	SearchResult search(SolrQuery query) throws Exception;
}
