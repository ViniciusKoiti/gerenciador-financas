package com.vinicius.gerenciamento_financeiro;
import com.vinicius.gerenciamento_financeiro.templates.TemplateEntidade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
public class CriarNovaEntidade {

    private static final String BASE_PATH = "src/main/java/com/vinicius/gerenciamento_financeiro/";

    public static void main(String[] args) {
        Scanner entidade = new Scanner(System.in);
        System.out.println("Digite o nome da Entidade: ");
        String nomeEntidade = entidade.next();
        criaEntidadeDomain(nomeEntidade);
        criaPortaRepository(nomeEntidade);
        criarEntidadeAdapter(nomeEntidade);

        entidade.close();
    }

    static void criaEntidadeDomain(String entidade){
        criarModel(entidade);
        criarService(entidade);
    }

    static void criarModel(String entidade){
        String pathModel = BASE_PATH + "domain/model/" + entidade.toLowerCase() + "/" + entidade + ".java";
        criarArquivoComConteudo(pathModel, TemplateEntidade.modelTemplate(entidade));
    }

    static void criarService(String entidade){
        String pathService = BASE_PATH + "domain/service/" + entidade.toLowerCase() + "/" + entidade + "Service" + ".java";
        criarArquivoComConteudo(pathService, TemplateEntidade.serviceTemplate(entidade));
    }

    static void criaPortaRepository(String entidade){
        criarPortaOut(entidade);
    }

    static void criarPortaOut(String entidade){
        String pathPortaOut = BASE_PATH + "port/out/" + entidade.toLowerCase() + "/" + entidade + "Repository" + ".java";
        criarArquivoComConteudo(pathPortaOut, TemplateEntidade.repositoryTemplate(entidade));
    }

    static void criarArquivoComConteudo(String path, String content) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }

            System.out.println("Arquivo criado: " + path);
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo: " + path + e);
        }
    }



    static void criarEntidadeAdapter(String entidade){
        String pathJpaRepo = BASE_PATH + "adapter/out/" + entidade.toLowerCase() + "/" + "Jpa" + entidade + "Repository" + ".java";
        String pathPersistenceAdapter = BASE_PATH + "adapter/out/" + entidade.toLowerCase() + "/" + entidade + "PersistenceAdapter" + ".java";

        String pathController = BASE_PATH + "adapter/in/web/controller/" + entidade.toLowerCase() + "/" + entidade + "Controller" + ".java";
        String pathPost = BASE_PATH + "adapter/in/web/request/" + entidade.toLowerCase() + "/" + entidade + "Post" + ".java";
        String pathPut = BASE_PATH + "adapter/in/web/request/" + entidade.toLowerCase() + "/" + entidade + "Put" + ".java";
        String pathResponse = BASE_PATH + "adapter/in/web/response/" + entidade.toLowerCase() + "/" + entidade + "Response" + ".java";
        String pathMapper = BASE_PATH + "adapter/in/web/mapper/" + entidade.toLowerCase() + "/" + entidade + "Mapper" + ".java";

        criarArquivoComConteudo(pathJpaRepo, TemplateEntidade.jpaRepositoryTemplate(entidade));
        criarArquivoComConteudo(pathPersistenceAdapter, TemplateEntidade.persistenceAdapterTemplate(entidade));
        criarArquivoComConteudo(pathController, TemplateEntidade.controllerTemplate(entidade));
        criarArquivoComConteudo(pathPost, TemplateEntidade.requestPostTemplate(entidade));
        criarArquivoComConteudo(pathPut, TemplateEntidade.requestPutTemplate(entidade));

    }


}
