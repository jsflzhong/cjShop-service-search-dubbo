package com.cj.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cj.core.service.ItemService;
import com.cj.common.pojo.TaotaoResult;
import com.cj.common.utils.ExceptionUtil;

/**
 * 把商品数据导入Solr索引库的Controller.
 * @author 崔健
 * @date 2016年12月13日上午12:36:48
 */
@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping(value="/importAll")
	@ResponseBody
	public TaotaoResult importAll() {
		try {
			TaotaoResult result = itemService.importItems();
			if (result != null) {
				return result;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		
	}
	
}
