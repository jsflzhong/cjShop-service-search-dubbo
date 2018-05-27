package com.cj.core.facade;

import com.alibaba.dubbo.config.annotation.Service;
import com.cj.core.pojo.TaotaoResult;
import com.cj.core.facade.search.ItemFacade;
import com.cj.core.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0")
public class ItemFacadeImpl implements ItemFacade{

	@Autowired
	private ItemService itemService;

	@Override
	public TaotaoResult importItems() throws Exception {
		return itemService.importItems();
	}
}
