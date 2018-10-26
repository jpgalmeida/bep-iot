package example.com.lockr.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import example.com.lockr.config.*;


@RestController
@RequestMapping("rest")
public class SectorPriceController {


	@RequestMapping(value = "/initPrice/{sector}/{price}", method = RequestMethod.POST)
	public ErrorInfo initSectorPrice(@PathVariable("sector") String sector, @PathVariable("price") String price) {
		
		String result = RunChannel.initPrice(sector, price);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	
	@RequestMapping(value = "/readSectorPrice/{sector}", method = RequestMethod.GET)
	public ResponseInfo readSectorPrice(@PathVariable("sector") String sector) {
		
		String result = RunChannel.readSectorPrice(sector);	
		
		ResponseInfo res = new ResponseInfo(0, "", result);
		return res;
	}
	
	@RequestMapping(value = "/getSectorsByRange/{startId}/{endId}", method = RequestMethod.POST)
	public ErrorInfo getSectorsByRange(@PathVariable("startId") String startId, @PathVariable("endId") String endId) {
		
		String result = RunChannel.getSectorsByRange(startId, endId);	
		ErrorInfo res = new ErrorInfo(0, "", result);
		return res;
	}
	
}
