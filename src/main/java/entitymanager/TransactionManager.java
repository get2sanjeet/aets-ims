package main.java.entitymanager;
import main.java.models.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TransactionManager {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static int sessionCallCount = 0;

    private static SessionFactory buildSessionFactory() {
        try {
            AnnotationConfiguration configuration = new AnnotationConfiguration();
            Properties settings = new Properties();

            settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/inventory");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "root");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
            settings.put(Environment.HBM2DDL_AUTO, "update");
            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.FORMAT_SQL, "true");
            configuration.setProperties(settings);

            configuration.addAnnotatedClass(Stock.class);
            configuration.addAnnotatedClass(ProductSpecification.class);
            configuration.addAnnotatedClass(SalesOrder.class);
            configuration.addAnnotatedClass(Product.class);
            configuration.addAnnotatedClass(Customer.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        System.out.println("Session call count = " + sessionCallCount++);
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

    public static void main(String[] args) {
        //saveOrder();
        final List<SalesOrder> orders = TransactionManager.getSalesOrder();
        orders.forEach(o->{
            System.out.println(o);
        });
    }

    private static Customer getCustomer(){
        Customer customer = new Customer();
        customer.setAddressOne("#323,Lamba Line, Port Blair 744101");
        customer.setName("Macchi");
        customer.setGender("Male");
        customer.setCreateDate(new Date(System.currentTimeMillis()));
        customer.setPhone("0556877626");
        customer.setEmail("erereew@gmail.com");
        return customer;
    }

    private static void saveOrder() {
        System.out.println("-----------CREATING PRODUCT--------------");
        getProducts();
        System.out.println("-----------PRODUCT CREATED--------------");

        System.out.println("-----------CREATING CUSTOMER--------------");
        TransactionManager.save(getCustomer());
        System.out.println("----------- CUSTOMER CREATED--------------");

        System.out.println("-----------CREATING SALES ORDER--------------");

        List<Product> products = new ArrayList<>();
        products.add(TransactionManager.getProductByBarcodeSerialNumber("0119076074409"));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setTax(Double.valueOf(130));
        salesOrder.setPrice(Double.valueOf(499));
        salesOrder.setTotalPrice(Double.valueOf(81));
        salesOrder.setProducts(products);
        salesOrder.setCustomer(TransactionManager.getCustomerById(1));
        List<SalesOrder> salesOrders = new ArrayList<>();
        salesOrders.add(salesOrder);

        TransactionManager.save(salesOrder);
        System.out.println("-----------SALES ORDER CREATED--------------");


    }

    private static void getProducts() {
        Product product = new Product();
        product.setCreated(false);
        product.setPerPeicePrice(1021);
        product.setProductModel("9W");
        product.setProductName("LED BULB");
        product.setBarcodeSerialNumber("0119076074409");
        TransactionManager.save(product);
    }

    private static List<SalesOrder> getSalesOrder() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(SalesOrder.class);
        List<SalesOrder> salesOrders = (List<SalesOrder>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return salesOrders;
    }

    public static List<Stock> getProductTypes() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Stock.class);
        List<Stock> productTypes = (List<Stock>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return productTypes;
    }

    public static List<Product> getAllProducts() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Product.class);
        List<Product> products = (List<Product>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return products;
    }

    public static List<Customer> getCustomers() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Customer.class);
        List<Customer> customers = (List<Customer>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return customers;
    }
    public static void deleteProductSpecification(int id) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProductSpecification.class);
        criteria.add(Restrictions.eq("id",id));
        ProductSpecification productSpecification = (ProductSpecification)criteria.uniqueResult();
        session.delete(productSpecification);
        session.getTransaction().commit();
        shutdown();
    }

    public static List<ProductSpecification> getProductSpecifications() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProductSpecification.class);
        List<ProductSpecification> productSpecifications = (List<ProductSpecification>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return productSpecifications;
    }

    public static void save(Stock productType) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(productType);
        session.getTransaction().commit();
        shutdown();
    }

    public static void save(Customer customer) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(customer);
        session.getTransaction().commit();
        shutdown();
    }
    public static void save(Product product) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
        shutdown();
    }

    public static void save(ProductSpecification productSpecification) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(productSpecification);
        session.getTransaction().commit();
        shutdown();
    }

    public static void update(ProductSpecification productSpecification){
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.merge(productSpecification);
        session.getTransaction().commit();
        shutdown();
    }

    public static Integer getMaximumProductId() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.setMaxResults(1);
        criteria.addOrder(org.hibernate.criterion.Order.desc("id"));
        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.property("id"));

        Integer result = (Integer) criteria.setProjection(projections).uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return result;
    }

    public static Integer getProductTypesByNameModel(String name, String model) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProductSpecification.class);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("model", model));
        ProductSpecification productSpecification = (ProductSpecification) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return productSpecification.getId();
    }

    public static Product getProductByBarcodeSerialNumber(String barcodeSerialNumber) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("barcodeSerialNumber",barcodeSerialNumber));
        Product product = (Product) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return product;
    }

    public static Product getProduct(int id){
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("id",id));
        Product product = (Product) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return product;
    }

    public static Customer getCustomerByName(String name) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Customer.class);
        criteria.add(Restrictions.eq("name", name));
        Customer customer = (Customer) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return customer;
    }
    public static List<Customer> getCustomerByLikeName(String name) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Customer.class);
        criteria.add(Restrictions.like("name", name));
        List<Customer> customers = (List<Customer>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return customers;
    }

    public static Customer getCustomerById(int id) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Customer.class);
        criteria.add(Restrictions.eq("id",id));
        Customer customer = (Customer) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return customer;
    }

    public static void save(SalesOrder order) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(order);
        session.getTransaction().commit();
        shutdown();
    }

    public static List<SalesOrder> getAllSalesOrders() {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(SalesOrder.class);
        List<SalesOrder> salesOrders = (List<SalesOrder>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return salesOrders;
    }

    public static SalesOrder getSalesOrderById(int id) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(SalesOrder.class);
        criteria.add(Restrictions.eq("id",id));
        SalesOrder salesOrder = (SalesOrder) criteria.uniqueResult();
        session.getTransaction().commit();
        shutdown();
        return salesOrder;
    }

    public static List<SalesOrder> getSalesOrdersByCustomerId(int id) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(SalesOrder.class);
        criteria.add(Restrictions.eq("customer",TransactionManager.getCustomerById(id)));
        List<SalesOrder> salesOrders = (List<SalesOrder>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return salesOrders;
    }

    public static List<Product> getProductByNameModel(String name, String model) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("productName", name));
        criteria.add(Restrictions.eq("productModel", model));
        List<Product> products = (List<Product>) criteria.list();
        session.getTransaction().commit();
        shutdown();
        return products;
    }

    public static void updateCustomer(Customer customer) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.merge(customer);
        session.getTransaction().commit();
        shutdown();
    }

    public static void saveCustomer(Customer customer) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(customer);
        session.getTransaction().commit();
        shutdown();
    }

    public static void update(List<Product> listOfScannedProducts) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        listOfScannedProducts.forEach( product -> session.update(product));
        session.getTransaction().commit();
        shutdown();
    }

    public static void updateProductAsSold(List<Integer> productIds) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        productIds.forEach( id -> {
            Product product =
                    (Product) session.get(Product.class, id);
            product.setSold( true );
            session.update(product);
        });
        session.getTransaction().commit();
        shutdown();
    }
}
