package com.marketplace.salesapi.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.marketplace.salesapi.order.model.Order;
import com.marketplace.salesapi.order.model.OrderItem;
import com.marketplace.salesapi.order.model.OrderPayment;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.model.PaymentMethod;
import com.marketplace.salesapi.order.model.PaymentStatus;
import com.marketplace.salesapi.order.model.ShippingInfo;
import com.marketplace.salesapi.order.repository.OrderItemRepository;
import com.marketplace.salesapi.order.repository.OrderPaymentRepository;
import com.marketplace.salesapi.order.repository.OrderRepository;
import com.marketplace.salesapi.order.repository.ShippingInfoRepository;
import com.marketplace.salesapi.product.model.Category;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.repository.CategoryRepository;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.RoleRepository;
import com.marketplace.salesapi.user.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private OrderPaymentRepository orderPaymentRepository;
    
    @Autowired
    private ShippingInfoRepository shippingInfoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles se não existirem
        for (ERole role : ERole.values()) {
            if (roleRepository.findByName(role).isEmpty()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
                System.out.println("Role criada: " + role);
            }
        }
        
        // Inicializar categorias padrão se não existirem
        initializeDefaultCategories();
        
        // Inicializar pedidos de teste (opcional, apenas para ambiente de desenvolvimento)
        initializeTestOrders();
    }
    
    private void initializeDefaultCategories() {
        // Lista de categorias principais
        List<String> mainCategories = Arrays.asList(
            "Eletrônicos", "Moda", "Casa e Decoração", "Esportes", "Beleza e Saúde",
            "Livros", "Brinquedos", "Alimentos", "Automotivo", "Jardim"
        );
        
        // Inicializar categorias principais
        for (String categoryName : mainCategories) {
            Optional<Category> existingCategory = categoryRepository.findByName(categoryName);
            if (existingCategory.isEmpty()) {
                Category category = new Category();
                category.setName(categoryName);
                category.setDescription("Categoria de " + categoryName);
                categoryRepository.save(category);
                System.out.println("Categoria criada: " + categoryName);
            }
        }
    }
    
    private void initializeTestOrders() {
        // Verificar se já existem pedidos
        if (orderRepository.count() > 0) {
            System.out.println("Pedidos já existem, pulando inicialização de pedidos de teste.");
            return;
        }
        
        // Obter usuários
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("Nenhum usuário encontrado, pulando inicialização de pedidos de teste.");
            return;
        }
        
        // Obter produtos
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            System.out.println("Nenhum produto encontrado, pulando inicialização de pedidos de teste.");
            return;
        }
        
        // Criar alguns pedidos de teste
        Random random = new Random();
        
        for (int i = 0; i < 10; i++) {
            User user = users.get(random.nextInt(users.size()));
            
            // Criar pedido
            Order order = new Order();
            order.setUser(user);
            order.setStatus(OrderStatus.values()[random.nextInt(OrderStatus.values().length)]);
            order.setShippingCost(new BigDecimal("15.00"));
            order.setNotes("Pedido de teste #" + (i + 1));
            
            Order savedOrder = orderRepository.save(order);
            
            // Adicionar itens ao pedido
            int numItems = random.nextInt(3) + 1; // 1 a 3 itens
            for (int j = 0; j < numItems; j++) {
                Product product = products.get(random.nextInt(products.size()));
                
                OrderItem item = new OrderItem();
                item.setOrder(savedOrder);
                item.setProduct(product);
                item.setProductName(product.getName());
                item.setProductSku(product.getSku());
                item.setQuantity(random.nextInt(3) + 1); // 1 a 3 unidades
                item.setUnitPrice(product.getPrice());
                
                orderItemRepository.save(item);
            }
            
            // Criar informações de envio
            ShippingInfo shippingInfo = new ShippingInfo();
            shippingInfo.setOrder(savedOrder);
            shippingInfo.setRecipientName(user.getName());
            shippingInfo.setStreet("Rua Teste");
            shippingInfo.setNumber("123");
            shippingInfo.setNeighborhood("Bairro Teste");
            shippingInfo.setCity("Cidade Teste");
            shippingInfo.setState("Estado Teste");
            shippingInfo.setZipCode("12345-678");
            shippingInfo.setCountry("Brasil");
            shippingInfo.setPhoneNumber("(11) 98765-4321");
            shippingInfo.setShippingMethod("Entrega Padrão");
            shippingInfo.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));
            
            if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
                shippingInfo.setShippedDate(LocalDateTime.now().minusDays(3));
                shippingInfo.setTrackingNumber("TRACK" + System.currentTimeMillis());
            }
            
            if (order.getStatus() == OrderStatus.DELIVERED) {
                shippingInfo.setDeliveredDate(LocalDateTime.now().minusDays(1));
            }
            
            shippingInfoRepository.save(shippingInfo);
            
            // Criar pagamento
            OrderPayment payment = new OrderPayment();
            payment.setOrder(savedOrder);
            payment.setPaymentMethod(PaymentMethod.values()[random.nextInt(PaymentMethod.values().length)]);
            
            // Definir status de pagamento com base no status do pedido
            switch (order.getStatus()) {
                case PENDING:
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    break;
                case CANCELLED:
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    break;
                default:
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    payment.setPaymentDate(LocalDateTime.now().minusDays(5));
                    payment.setTransactionId("TX" + System.currentTimeMillis());
                    break;
            }
            
            payment.setAmount(savedOrder.getTotal());
            
            orderPaymentRepository.save(payment);
            
            // Recalcular totais do pedido
            savedOrder.recalculateOrderTotals();
            orderRepository.save(savedOrder);
            
            System.out.println("Pedido de teste criado: #" + savedOrder.getOrderNumber());
        }
    }
}