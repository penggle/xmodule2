package com.penglecode.xmodule.java8.newfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.penglecode.xmodule.java8.model.Product;

public class OptionalExample {

	public static void main(String[] args) {
		testGetProductById();
	}
	
	public static void testGetProductById() {
		ProductService productService = new ProductServiceImpl();
		Optional<Product> product1 = productService.getProductById(Optional.ofNullable(null));
		product1.ifPresent(product -> {
			System.out.println("getProductById(null) = " + product);
		});
		System.out.println(product1.orElse(null));
		
		Optional<Product> product2 = productService.getProductById(Optional.ofNullable(123L));
		product2.ifPresent(product -> {
			System.out.println("getProductById(123) = " + product);
		});
		
		Optional<Product> product3 = productService.getProductById(Optional.ofNullable(2L));
		product3.ifPresent(product -> {
			System.out.println("getProductById(2) = " + product);
		});
	}
	
	interface ProductService {
		
		Optional<Product> getProductById(Optional<Long> productId);
		
		Optional<List<Product>> getProductListByType(Optional<String> productType);
		
	}
	
	static class ProductServiceImpl implements ProductService {

		private ProductRepository repository = new ProductRepository();
		
		public ProductRepository getRepository() {
			return repository;
		}

		public void setRepository(ProductRepository repository) {
			this.repository = repository;
		}

		public Optional<Product> getProductById(Optional<Long> productId) {
			return productId.flatMap(pId -> {
				return Optional.ofNullable(getRepository().getProductById(pId));
			});
		}

		public Optional<List<Product>> getProductListByType(Optional<String> productType) {
			return productType.flatMap(pType -> {
				return Optional.ofNullable(getRepository().getProductListByType(pType));
			});
		}
		
	}
	
	static class ProductRepository {
		
		private List<Product> ALL_PRODUCT_LIST = new ArrayList<Product>();
		
		{
			ALL_PRODUCT_LIST.add(new Product(1L, "?????? Mate 9 Pro", 4709.0, 13, "1"));
			ALL_PRODUCT_LIST.add(new Product(2L, "?????? iPhone 7 Plus 5.5???", 5056.0, 3, "1"));
			ALL_PRODUCT_LIST.add(new Product(3L, "?????? iPhone 6 4.7???", 2999.0, 0, "1"));
			ALL_PRODUCT_LIST.add(new Product(4L, "MyCard ??????/?????? 300?????????????????????", 67.8, 11, "0"));
			ALL_PRODUCT_LIST.add(new Product(5L, "OPPO R9?????? ?????????", 2168.0, 5, "1"));
			ALL_PRODUCT_LIST.add(new Product(6L, "Letv/?????? ???Pro3?????????", 1799.0, 74, "1"));
			ALL_PRODUCT_LIST.add(new Product(7L, "Xiaomi/?????? 5S Plus", 1599.0, 0, "1"));
			ALL_PRODUCT_LIST.add(new Product(8L, "?????????????????????100???", 98.0, 0, "0"));
		}
		
		public Product getProductById(Long productId) {
			return ALL_PRODUCT_LIST.stream().filter(product -> product.getProductId().equals(productId)).findFirst().orElse(null);
			//return getSqlSessionTemplate().selectOne(getMapperKey("getProductById"), productId); //??????mybatis????????????????????????
		}

		public List<Product> getProductListByType(String productType) {
			return ALL_PRODUCT_LIST.stream().filter(product -> product.getProductType().equals(productType)).collect(Collectors.toList());
			//return getSqlSessionTemplate().selectList(getMapperKey("getProductListByType"), productType); //??????mybatis????????????????????????
		}
		
	}

}
