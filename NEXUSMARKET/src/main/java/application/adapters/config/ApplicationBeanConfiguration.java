package application.adapters.config;

import application.ports.input.BuyerQueryUseCase;
import application.ports.input.RegisterBuyerUseCase;
import application.ports.output.UserRepository;
import application.services.BuyerQueryService;
import application.services.RegisterBuyerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    public RegisterBuyerUseCase registerBuyerUseCase(
            UserRepository userRepository
    ) {
        return new RegisterBuyerService(
                userRepository
        );
    }

    @Bean
    public BuyerQueryUseCase buyerQueryUseCase(
            UserRepository userRepository
    ) {
        return new BuyerQueryService(
                userRepository
        );
    }
}