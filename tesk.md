PROJETO: MÓDULO DE EVOLUÇÃO CLÍNICA (PDF + GRÁFICOS)

CONTEXTO ATUAL:

TAREFAS BACKEND (JAVA):

Adicionar dependência jfreechart e itextpdf no pom.xml.

Criar RelatorioService para gerar gráfico de linha (Data vs Simetria).

Criar endpoint /api/v1/pacientes/{id}/relatorio-pdf retornando um byte[].

Liberar essa rota no SecurityConfig e no JwtAuthFilter.

1. Backend (IntelliJ) - O Gráfico no PDF
   Objetivo: Gerar o gráfico como imagem e embutir no relatório.

[ ] Dependências: Adicionar jfreechart e itextpdf ao pom.xml.

[ ] Service de Relatório: Criar ou atualizar o RelatorioService para:

Buscar o histórico de avaliações do paciente por ID.

Criar um DefaultCategoryDataset com as datas e os valores de simetria.

Gerar o gráfico com ChartFactory.createLineChart.

Adicionar uma ValueMarker em 90.0 no eixo Y (Linha de Meta).

Converter o gráfico para imagem e inserir no documento iText.

[ ] Controller: Criar o endpoint GET /api/v1/pacientes/{id}/pdf retornando MediaType.APPLICATION_PDF.

[ ] Security: Garantir que a rota do PDF esteja liberada no SecurityConfig e no JwtAuthFilter.