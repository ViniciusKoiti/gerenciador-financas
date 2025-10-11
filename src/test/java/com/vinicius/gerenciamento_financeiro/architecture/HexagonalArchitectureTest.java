package com.vinicius.gerenciamento_financeiro.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@DisplayName("Hexagonal Architecture Tests")
class HexagonalArchitectureTest {

    private JavaClasses importedClasses;

    @BeforeEach
    void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vinicius.gerenciamento_financeiro");
    }

    @Test
    @DisplayName("Domain deve ser independente de outras camadas")
    void domainDeveSerIndependenteDeOutrasCamadas() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..adapter..",
                        "..port..",
                        "..application..",
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence.."
                )
                .because("Domain deve ser puro e independente de frameworks")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Adapters de entrada devem depender apenas de ports de entrada")
    void adaptersEntradaDevemDependerApenasDePortsEntrada() {
        classes()
                .that().resideInAPackage("..adapter.in..")
                .and().areNotAnnotatedWith(Configuration.class)
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..adapter.in..",
                        "..port.in..",
                        "..application..",
                        "..domain.model..",
                        "..domain.exception..",
                        "java..",
                        "org.springframework..",
                        "jakarta..",
                        "com.fasterxml.jackson..",
                        "org.mapstruct..",
                        "lombok..",
                        "io.swagger.v3.oas.annotations..",
                        "org.slf4j.."
                )
                .because("Adapters de entrada devem usar apenas ports de entrada")
                .check(importedClasses);
    }
    @Test
    @DisplayName("Adapters de saÃ­da devem implementar ports de saÃ­da")
    void adaptersSaidaDevemImplementarPortsSaida() {
        classes()
                .that().resideInAPackage("..adapter.out.persistence..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..adapter.out.persistence..",
                        "..adapter.out.persistence..entity..",
                        "..adapter.out.persistence..mapper..",
                        "..port.out..",
                        "..domain.model..",
                        "java..",
                        "org.springframework..",
                        "jakarta..",
                        "org.mapstruct..",
                        "lombok..",
                        "org.slf4j.."
                )
                .allowEmptyShould(true)
                .because("Adapters de saÃ­da devem implementar apenas ports de saÃ­da")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Ports nÃ£o devem depender de adapters")
    void portsNaoDevemDependerDeAdapters() {
        noClasses()
                .that().resideInAPackage("..port..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .because("Ports definem contratos e nÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o devem conhecer implementaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âµes")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Arquitetura em camadas deve ser respeitada")
    void arquiteturaEmCamadasDeveSerRespeitada() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()

                .layer("Core").definedBy("..domain..")

                .layer("Application").definedBy("..application..")
                .layer("Adapters").definedBy("..adapter..")

                .whereLayer("Core").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Core")
                .whereLayer("Adapters").mayOnlyAccessLayers("Application", "Core")
                
                .because("Arquitetura hexagonal deve ter dependÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âªncias direcionais corretas")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Entidades de domÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nio devem estar no pacote model")
    void entidadesDominioDevemEstarNoPacoteModel() {
        classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Entity")
                .or().haveSimpleNameEndingWith("VO")
                .or().haveSimpleNameEndingWith("ValueObject")
                .should().resideInAPackage("..domain.model..")
                .allowEmptyShould(true) // Permite passar se nÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o encontrar classes
                .because("Entidades e Value Objects de domÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nio devem estar no domain.model")
                .check(importedClasses);
    }

    @Test
    @DisplayName("ServiÃ§os de domÃ­nio devem estar no pacote service")
    void servicosDominioDevemEstarNoPacoteService() {
        classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.service..")
                .allowEmptyShould(true) // Permite o teste passar mesmo sem encontrar classes
                .because("ServiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§os de domÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nio devem estar organizados adequadamente")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Controllers devem estar no adapter de entrada web")
    void controllersDevemEstarNoAdapterEntradaWeb() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..adapter.in.web..")
                .because("Controllers sÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o adapters de entrada")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Repositories devem estar no adapter de saÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­da")
    void repositoriesDevemEstarNoAdapterSaida() {
        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("..adapter.out..")
                .because("ImplementaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âµes de repository sÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o adapters de saÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­da")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Ports de entrada devem ser interfaces")
    void portsEntradaDevemSerInterfaces() {
        classes()
                .that().resideInAPackage("..port.in..")
                .should().beInterfaces()
                .because("Ports de entrada devem definir contratos")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Ports de saÃ­da devem ser interfaces")
    void portsSaidaDevemSerInterfaces() {
        classes()
                .that().resideInAPackage("..port.out..")
                .should().beInterfaces()
                .because("Ports de saÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­da devem definir contratos")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Classes de domÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nio nÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o devem usar anotaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âµes Spring")
    void classesDominioNaoDevemUsarAnotacoesSpring() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .orShould().beAnnotatedWith("org.springframework.context.annotation.Configuration")
                .orShould().beAnnotatedWith("org.springframework.context.annotation.Bean")
                .because("Domain deve ser independente de frameworks")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Ports não devem depender de tipos de adapters")
    void portsNaoDevemDependerDeTiposDeAdapters() {
        noClasses()
                .that().resideInAPackage("..port..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..adapter.in.web.request..",
                        "..adapter.in.web.response..",
                        "..adapter.out.persistence.."
                )
                .because("Ports devem usar apenas tipos de domÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nio")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Application Services devem estar no pacote correto")
    void applicationServicesDevemEstarNoPacoteCorreto() {
        classes()
                .that().haveSimpleNameContaining("Service")
                .and().resideInAPackage("..application..")
                .should().resideInAPackage("..application.service..")
                .because("Application Services devem estar organizados adequadamente")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Domain Services devem implementar Use Cases")
    void domainServicesDevemImplementarUseCases() {
        classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .should().implement(CategoriaUseCase.class)
                .allowEmptyShould(true)
                .because("Domain Services devem implementar contratos de Use Cases")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Value Objects devem ser imutaveis")
    void valueObjectsDevemSerImutaveis() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.model..")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Builder")
                .should().beFinal()
                .because("Value Objects devem ser imutÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡veis")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Não deve haver depencidencias circulares entre pacotes")
    void naoDeveHaverDependenciasCirculares() {
        slices()
                .matching("com.vinicius.gerenciamento_financeiro.(*)..")
                .should().beFreeOfCycles()
                .because("Arquitetura deve ser livre de dependÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âªncias circulares")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Mappers devem estar nos pacotes de adapters")
    void mappersDevemEstarNoPacoteCorreto() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().areNotInterfaces()
                .should().resideInAnyPackage(
                        "..adapter.in.web.mapper..",
                        "..adapter.in.web.response..",
                        "..adapter.out.persistence.."
                )
                .because("Mappers devem estar nos pacotes de adapters (entrada ou saÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­da)")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Configs devem estar nos pacotes de adapters")
    void configsDevemEstarNoPacoteCorreto() {
        classes()
                .that().haveSimpleNameEndingWith("Config")
                .or().areAnnotatedWith(Configuration.class)
                .should().resideInAnyPackage(
                        "..adapter.out.config..",
                        "..adapter.in.web.config..",
                        "..adapter.out.criptografia..",
                        "..adapter.out.messaging.."
                )
                .because("ConfiguraÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âµes devem estar nos pacotes de adapters")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Use Cases devem ser interfaces")
    void useCasesDevemSerInterfaces() {
        classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .should().beInterfaces()
                .because("Use Cases definem contratos de entrada")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Domain deve depender apenas de si mesmo")
    void domainDeveDependerApenasDeleMesmo() {
        classes()
                .that().resideInAPackage("..domain..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..domain..",
                        "java..",
                        "lombok..",
                        "org.slf4j..",
                        "org.springframework.data.domain..",  // Permitido apenas para Pageable
                        "org.springframework.http..",  // Permitido para ResponseStatusException
                        "org.springframework.web.server.."  // Permitido para ResponseStatusException
                )
                .because("Domain deve ser independente e puro")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Domain Services devem implementar Use Cases corretamente")
    void domainServicesDevemImplementarUseCasesCorretamente() {
        classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .and().resideInAPackage("..domain.service..")
                .should().bePublic()
                .andShould().haveOnlyFinalFields()
                .allowEmptyShould(true) // Permite o teste passar mesmo sem encontrar classes
                .because("Domain Services devem ser bem estruturados")
                .check(importedClasses);
    }
}