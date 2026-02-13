# -*- coding: utf-8 -*-
"""
Gerador de PDF para documentos
Converte o conteúdo formatado em PDF profissional
"""

import os
from pathlib import Path
from datetime import datetime
from typing import Dict, Optional, Tuple
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm, inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle
from reportlab.platypus.flowables import HRFlowable
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_RIGHT, TA_LEFT
from reportlab.pdfgen import canvas


class PDFGenerator:
    """Gera PDFs profissionais a partir do conteúdo dos documentos"""
    
    def __init__(self):
        """Inicializa o gerador de PDF"""
        self.output_dir = Path("documentos_gerados")
        self.output_dir.mkdir(exist_ok=True)
        self.styles = self._create_styles()
        
    def generate(self, document_data: Dict, metadata: Dict) -> str:
        """
        Gera um PDF a partir dos dados do documento
        
        Args:
            document_data: Dados do documento (content, title, type, etc)
            metadata: Metadados adicionais
            
        Returns:
            Caminho do arquivo PDF gerado
        """
        # Define o nome do arquivo
        filename = self._generate_filename(document_data, metadata)
        filepath = self.output_dir / filename
        
        # Cria o documento PDF
        doc = SimpleDocTemplate(
            str(filepath),
            pagesize=A4,
            rightMargin=20*mm,
            leftMargin=20*mm,
            topMargin=25*mm,
            bottomMargin=25*mm,
            title=document_data.get('title', 'Documento'),
            author='Contrauto - Sistema de Geração de Documentos',
            subject=f"{document_data.get('type', 'Documento')} Comercial",
            creator='Contrauto',
            producer='Contrauto PDF Generator'
        )
        
        # Constrói o conteúdo
        story = self._build_content(document_data, metadata)
        
        # Gera o PDF
        doc.build(story, onFirstPage=self._add_header_footer, onLaterPages=self._add_header_footer)
        
        return str(filepath)
        
    def _create_styles(self) -> Dict:
        """Cria estilos personalizados para o documento"""
        styles = getSampleStyleSheet()
        
        # Estilo para título principal
        styles.add(ParagraphStyle(
            name='CustomTitle',
            parent=styles['Title'],
            fontSize=18,
            textColor=colors.HexColor('#1a1a1a'),
            spaceAfter=30,
            alignment=TA_CENTER,
            fontName='Helvetica-Bold'
        ))
        
        # Estilo para cabeçalhos de seção
        styles.add(ParagraphStyle(
            name='SectionHeader',
            parent=styles['Heading1'],
            fontSize=14,
            textColor=colors.HexColor('#2c3e50'),
            spaceAfter=12,
            fontName='Helvetica-Bold',
            leftIndent=0
        ))
        
        # Estilo para subcabeçalhos
        styles.add(ParagraphStyle(
            name='SubHeader',
            parent=styles['Heading2'],
            fontSize=12,
            textColor=colors.HexColor('#34495e'),
            spaceAfter=10,
            fontName='Helvetica-Bold'
        ))
        
        # Estilo para corpo do texto
        styles.add(ParagraphStyle(
            name='CustomBody',
            parent=styles['BodyText'],
            fontSize=11,
            textColor=colors.HexColor('#2c3e50'),
            alignment=TA_JUSTIFY,
            spaceAfter=12,
            leading=14
        ))
        
        # Estilo para informações de contato
        styles.add(ParagraphStyle(
            name='ContactInfo',
            parent=styles['Normal'],
            fontSize=10,
            textColor=colors.HexColor('#555555'),
            spaceAfter=6,
            leftIndent=20
        ))
        
        # Estilo para assinaturas
        styles.add(ParagraphStyle(
            name='Signature',
            parent=styles['Normal'],
            fontSize=10,
            alignment=TA_CENTER,
            spaceAfter=40
        ))
        
        # Estilo para data
        styles.add(ParagraphStyle(
            name='DateStyle',
            parent=styles['Normal'],
            fontSize=11,
            alignment=TA_RIGHT,
            textColor=colors.HexColor('#555555'),
            spaceAfter=20
        ))
        
        return styles
        
    def _build_content(self, document_data: Dict, metadata: Dict) -> list:
        """Constrói o conteúdo do PDF"""
        story = []
        content = document_data.get('content', '')
        doc_type = document_data.get('type', 'document')
        
        # Processa o conteúdo linha por linha
        lines = content.split('\n')
        
        for line in lines:
            line = line.strip()
            
            if not line:
                # Linha vazia - adiciona espaço
                story.append(Spacer(1, 6*mm))
                
            elif line.upper() == line and len(line) > 10 and not any(char.isdigit() for char in line):
                # Título em maiúsculas
                if 'PROPOSTA COMERCIAL' in line or 'CONTRATO' in line:
                    story.append(Paragraph(line, self.styles['CustomTitle']))
                else:
                    story.append(Paragraph(line, self.styles['SectionHeader']))
                    
            elif line.startswith(('1.', '2.', '3.', '4.', '5.', '6.', '7.', '8.', '9.', '10.', '11.', '12.', '13.', '14.', '15.')):
                # Cabeçalhos numerados
                story.append(Spacer(1, 3*mm))
                story.append(Paragraph(line, self.styles['SectionHeader']))
                
            elif line.startswith('CLÁUSULA'):
                # Cláusulas de contrato
                story.append(Spacer(1, 4*mm))
                story.append(Paragraph(line, self.styles['SubHeader']))
                
            elif line.startswith(('•', '-', '*')):
                # Itens de lista
                story.append(Paragraph(line, self.styles['CustomBody']))
                
            elif line.startswith('_____'):
                # Linha de assinatura
                story.append(Spacer(1, 10*mm))
                story.append(HRFlowable(width="40%", thickness=0.5, color=colors.black, spaceAfter=2*mm))
                
            elif any(term in line for term in ['E-mail:', 'Telefone:', 'NIF:', 'CNPJ:', 'CPF:', 'NIPC:']):
                # Informações de contato
                story.append(Paragraph(line, self.styles['ContactInfo']))
                
            elif line.startswith(('Data:', 'Validade:')):
                # Datas
                story.append(Paragraph(line, self.styles['DateStyle']))
                
            else:
                # Texto normal
                story.append(Paragraph(line, self.styles['CustomBody']))
        
        return story
        
    def _add_header_footer(self, canvas_obj, doc):
        """Adiciona cabeçalho e rodapé às páginas"""
        canvas_obj.saveState()
        
        # Rodapé
        canvas_obj.setFont('Helvetica', 8)
        canvas_obj.setFillColor(colors.HexColor('#888888'))
        
        # Número da página
        page_num = canvas_obj.getPageNumber()
        text = f"Página {page_num}"
        canvas_obj.drawRightString(A4[0] - 20*mm, 15*mm, text)
        
        # Texto do rodapé
        canvas_obj.drawString(20*mm, 15*mm, "Documento gerado por Contrauto - Sistema de Geração de Documentos")
        
        # Linha separadora do rodapé
        canvas_obj.setStrokeColor(colors.HexColor('#cccccc'))
        canvas_obj.line(20*mm, 20*mm, A4[0] - 20*mm, 20*mm)
        
        canvas_obj.restoreState()
        
    def _generate_filename(self, document_data: Dict, metadata: Dict) -> str:
        """Gera o nome do arquivo PDF"""
        doc_type = document_data.get('type', 'documento')
        
        # Obtém informações relevantes
        if doc_type == 'proposal':
            client = metadata.get('contractor_name', 'Cliente')
            service = metadata.get('service_title', 'Servico')
            base_name = f"Proposta_{client}_{service}"
        else:  # contract
            contractor = metadata.get('contractor_name', 'Contratante')
            contracted = metadata.get('contracted_name', 'Contratado')
            base_name = f"Contrato_{contractor}_{contracted}"
            
        # Remove caracteres inválidos
        base_name = "".join(c for c in base_name if c.isalnum() or c in ('_', '-'))
        
        # Adiciona timestamp
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        
        return f"{base_name}_{timestamp}.pdf"
        
    def preview_html(self, document_data: Dict) -> str:
        """
        Gera uma versão HTML do documento para preview
        
        Args:
            document_data: Dados do documento
            
        Returns:
            String HTML
        """
        content = document_data.get('content', '')
        title = document_data.get('title', 'Documento')
        
        # Template HTML básico
        html = f"""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>{title}</title>
            <style>
                body {{
                    font-family: Arial, sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    line-height: 1.6;
                    color: #333;
                }}
                h1 {{ color: #1a1a1a; text-align: center; }}
                h2 {{ color: #2c3e50; margin-top: 30px; }}
                h3 {{ color: #34495e; }}
                .signature {{ 
                    margin-top: 40px; 
                    border-bottom: 1px solid #000;
                    width: 300px;
                    display: inline-block;
                }}
                .date {{ text-align: right; color: #666; }}
                .contact-info {{ margin-left: 20px; color: #555; }}
                hr {{ border: none; border-top: 1px solid #ddd; margin: 20px 0; }}
            </style>
        </head>
        <body>
        """
        
        # Processa o conteúdo
        lines = content.split('\n')
        for line in lines:
            line = line.strip()
            
            if not line:
                html += "<br>"
            elif line.upper() == line and len(line) > 10:
                html += f"<h1>{line}</h1>"
            elif line.startswith(('1.', '2.', '3.', '4.', '5.', '6.', '7.', '8.', '9.')):
                html += f"<h2>{line}</h2>"
            elif line.startswith('CLÁUSULA'):
                html += f"<h3>{line}</h3>"
            elif line.startswith('_____'):
                html += '<div class="signature"></div><br>'
            elif any(term in line for term in ['E-mail:', 'Telefone:', 'NIF:', 'CNPJ:']):
                html += f'<div class="contact-info">{line}</div>'
            elif line.startswith(('Data:', 'Validade:')):
                html += f'<div class="date">{line}</div>'
            else:
                html += f"<p>{line}</p>"
                
        html += """
        </body>
        </html>
        """
        
        return html
