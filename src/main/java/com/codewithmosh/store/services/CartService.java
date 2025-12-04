package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.mappers.CartMappers;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {
    private  CartRepository cartRepository;
    private  CartMappers cartMappers;
    private  ProductRepository productRepository;

    public CartDto createCart(){
       var cart = new Cart();
       cartRepository.save(cart);
       return cartMappers.toCartDto(cart);
    }
        public CartDto addToCart(UUID cartId, Long productId){
        // Try to find the cart in the database using its ID
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {

            throw new CartNotFoundException();
        }

        // Try to find the product in the database using the productId from the request
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {

            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);

        // Save the cart (will also save cart items because of cascade)
        cartRepository.save(cart);

        // Convert CartItem entity into a DTO for response
       return cartMappers.toCartDto(cart);

    }
    public CartDto getCart(UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        return cartMappers.toCartDto(cart);

    }
    public CartItemDto updateItem( UUID cartId, Long productId, Integer quantity){
        var cart =  cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
           throw new CartNotFoundException();
        }
        var cartItem = cart.getItem(productId);

        if (cartItem == null) {
            throw new ProductNotFoundException();
        }
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMappers.toDto(cartItem);

    }
    public void removeItem(UUID cartId, Long productId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeItem(productId);
        cartRepository.save(cart);


    }
    public void clearCart(UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
