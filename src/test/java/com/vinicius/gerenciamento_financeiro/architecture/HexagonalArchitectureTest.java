package com.vinicius.gerenciamento_financeiro.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..port.in..",
                        "..domain.model..",
                        "java..",
                        "org.springframework..",
                        "jakarta..",
                        "com.fasterxml.jackson..",
                        "org.mapstruct..",
                        "lombok..",
                        "io.swagger.v3.oas.annotations.."
                )
                .because("Adapters de entrada devem usar apenas ports de entrada")
                .check(importedClasses);
    }
    @Test
    @DisplayName("Adapters de saída devem implementar ports de saída")
    void adaptersSaidaDevemImplementarPortsSaida() {
        classes()
                .that().resideInAPackage("..adapter.out..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "..port.out..",
                        "..domain.model..",
                        "java..",
                        "org.springframework..",
                        "jakarta..",
                        "org.mapstruct.."
                )
                .because("Adapters de saída devem implementar apenas ports de saída")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Ports não devem depender de adapters")
    void portsNaoDevemDependerDeAdapters() {
        noClasses()
                .that().resideInAPackage("..port..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .because("Ports definem contratos e não devem conhecer implementações")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Arquitetura em camadas deve ser respeitada")
    void arquiteturaEmCamadasDeveSerRespeitada() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("..domain..")
                .layer("Ports").definedBy("..port..")
                .layer("Adapters").definedBy("..adapter..")
                
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Ports").mayOnlyAccessLayers("Domain")
                .whereLayer("Adapters").mayOnlyAccessLayers("Ports", "Domain")
                
                .because("Arquitetura hexagonal deve ter dependências direcionais corretas")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Entidades de domínio devem estar no pacote model")
    void entidadesDominioDevemEstarNoPacoteModel() {
        classes()
                .that().haveSimpleNameEndingWith("Entity")
                .or().haveSimpleNameEndingWith("VO")
                .or().haveSimpleNameEndingWith("ValueObject")
                .should().resideInAPackage("..domain.model..")
                .because("Entidades e Value Objects devem estar no domain.model")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Serviços de domínio devem estar no pacote service")
    void servicosDominioDevemEstarNoPacoteService() {
        classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .or().haveSimpleNameContaining("Service")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.service..")
                .because("Serviços de domínio devem estar organizados adequadamente")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Controllers devem estar no adapter de entrada web")
    void controllersDevemEstarNoAdapterEntradaWeb() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..adapter.in.web..")
                .because("Controllers são adapters de entrada")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Repositories devem estar no adapter de saída")
    void repositoriesDevemEstarNoAdapterSaida() {
        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("..adapter.out..")
                .because("Implementações de repository são adapters de saída")
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
    @DisplayName("Ports de saída devem ser interfaces")
    void portsSaidaDevemSerInterfaces() {
        classes()
                .that().resideInAPackage("..port.out..")
                .should().beInterfaces()
                .because("Ports de saída devem definir contratos")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Classes de domínio não devem usar anotações Spring")
    void classesDominioNaoDevemUsarAnotacoesSpring() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .because("Domain deve ser independente de frameworks")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Value Objects devem ser imutáveis")
    void valueObjectsDevemSerImutaveis() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.model..")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Builder")
                .should().beFinal()
                .because("Value Objects devem ser imutáveis")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Não deve haver dependências circulares entre pacotes")
    void naoDeveHaverDependenciasCirculares() {
        slices()
                .matching("com.vinicius.gerenciamento_financeiro.(*)..")
                .should().beFreeOfCycles()
                .because("Arquitetura deve ser livre de dependências circulares")
                .check(importedClasses);
    }
}