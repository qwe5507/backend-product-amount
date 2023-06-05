SET REFERENTIAL_INTEGRITY FALSE; -- 모든 제약조건 비활성화
truncate table product;
truncate table promotion;
truncate table promotion_products;
SET REFERENTIAL_INTEGRITY TRUE; -- 모든 제약조건 활성화