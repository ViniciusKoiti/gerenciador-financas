package com.vinicius.gerenciamento_financeiro.templates;

public class TemplateEntidade {
    public static String modelTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.domain.model.%s;
            
            import jakarta.persistence.Entity;
            import jakarta.persistence.GeneratedValue;
            import jakarta.persistence.GenerationType;
            import jakarta.persistence.Id;
            import jakarta.persistence.Table;
            import lombok.Data;
            
            @Entity
            @Table(name = "%s")
            @Data
            public class %s {
                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;
                
                // TODO: Adicione seus atributos aqui
            }
            """.formatted(entidade.toLowerCase(), entidade.toLowerCase(), entidade);
    }

    public static String serviceTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.domain.service.%s;
            
            import com.vinicius.gerenciamento_financeiro.domain.model.%s.%s;
            import org.springframework.stereotype.Service;
            import java.util.List;
            
            @Service
            public class %sService {
                
                // TODO: Adicione as dependências necessárias
                
                public %s save(%s entity) {
                    // TODO: Implemente a lógica de salvamento
                    return entity;
                }
                
                public %s findById(Long id) {
                    // TODO: Implemente a lógica de busca
                    return null;
                }
                
                public List<%s> findAll() {
                    // TODO: Implemente a lógica de listagem
                    return List.of();
                }
                
                public void delete(Long id) {
                    // TODO: Implemente a lógica de deleção
                }
            }
            """.formatted(
                entidade.toLowerCase(), entidade.toLowerCase(), entidade,
                entidade,
                entidade, entidade,
                entidade,
                entidade
        );
    }

    public static String repositoryTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.port.out.%s;
            
            import com.vinicius.gerenciamento_financeiro.domain.model.%s.%s;
            import java.util.List;
            import java.util.Optional;
            
            public interface %sRepository {
                %s save(%s entity);
                Optional<%s> findById(Long id);
                List<%s> findAll();
                void deleteById(Long id);
            }
            """.formatted(
                entidade.toLowerCase(), entidade.toLowerCase(), entidade,
                entidade,
                entidade, entidade,
                entidade,
                entidade
        );
    }

    public static String jpaRepositoryTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.adapter.out.%s;
            
            import com.vinicius.gerenciamento_financeiro.domain.model.%s.%s;
            import org.springframework.data.jpa.repository.JpaRepository;
            
            public interface Jpa%sRepository extends JpaRepository<%s, Long> {
            }
            """.formatted(
                entidade.toLowerCase(), entidade.toLowerCase(), entidade,
                entidade, entidade
        );
    }

    public static String persistenceAdapterTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.adapter.out.%s;
            
            import com.vinicius.gerenciamento_financeiro.domain.model.%s.%s;
            import com.vinicius.gerenciamento_financeiro.port.out.%s.%sRepository;
            import lombok.RequiredArgsConstructor;
            import org.springframework.stereotype.Component;
            
            @Component
            @RequiredArgsConstructor
            public class %sPersistenceAdapter implements %sRepository {
                private final Jpa%sRepository repository;
            }
            """.formatted(
                entidade.toLowerCase(), entidade.toLowerCase(), entidade,
                entidade.toLowerCase(), entidade,
                entidade, entidade, entidade
        );
    }

    public static String controllerTemplate(String entidade) {
        return """
            package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.%s;
            
            import com.vinicius.gerenciamento_financeiro.domain.model.%s.%s;
            import lombok.RequiredArgsConstructor;
            import org.springframework.web.bind.annotation.*;
            
            @RestController
            @RequestMapping("/api/%s")
            @RequiredArgsConstructor
            public class %sController {
            }
            """.formatted(
                entidade.toLowerCase(), entidade.toLowerCase(), entidade,
                entidade,
                entidade
        );
    }

    public static String requestPostTemplate(String entidade) {
        return """
        package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.%s;
        
        import jakarta.validation.constraints.NotNull;
        import lombok.Getter;
        import lombok.Setter;
        
        @Getter
        @Setter
        public class %sPost {
            // TODO: Adicione os atributos necessários para criação
            
            @NotNull(message = "Id não deve ser enviado na criação ")
            private Long id;
            
     
        }
        """.formatted(entidade.toLowerCase(), entidade);
    }

    public static String requestPutTemplate(String entidade) {
        return """
        package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.%s;
        
        import jakarta.validation.constraints.NotNull;
        import lombok.Getter;
        import lombok.Setter;
        
        @Getter
        @Setter
        public class %sPut {
            // TODO: Adicione os atributos necessários para criação
            
            @NotNull(message = "O id é necessario para atualizar ")
            private Long id;
            
     
        }
        """.formatted(entidade.toLowerCase(), entidade);
    }
}