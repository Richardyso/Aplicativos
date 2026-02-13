# -*- coding: utf-8 -*-
"""
Gerenciador de placeholders
Substitui marcadores no template pelos valores reais
"""

import re
from typing import Dict, List, Optional, Set
from datetime import datetime


class PlaceholderManager:
    """Gerencia a substituição de placeholders em templates"""
    
    def __init__(self):
        """Inicializa o gerenciador de placeholders"""
        self.placeholder_pattern = re.compile(r'\{([^}]+)\}')
        self.conditional_pattern = re.compile(r'\{\?([^:]+):([^}]*)\}')
        self.list_pattern = re.compile(r'\{#([^:]+):([^}]*)\}')
        
    def replace(self, template: str, data: Dict) -> str:
        """
        Substitui todos os placeholders no template
        
        Args:
            template: Template com placeholders
            data: Dicionário com os valores para substituição
            
        Returns:
            Texto com placeholders substituídos
        """
        # Processa condicionais primeiro
        result = self._process_conditionals(template, data)
        
        # Processa listas
        result = self._process_lists(result, data)
        
        # Substitui placeholders simples
        result = self._replace_simple_placeholders(result, data)
        
        return result
        
    def find_placeholders(self, template: str) -> Set[str]:
        """
        Encontra todos os placeholders em um template
        
        Args:
            template: Template para análise
            
        Returns:
            Conjunto com nomes dos placeholders encontrados
        """
        placeholders = set()
        
        # Placeholders simples
        for match in self.placeholder_pattern.finditer(template):
            placeholders.add(match.group(1))
            
        # Placeholders condicionais
        for match in self.conditional_pattern.finditer(template):
            placeholders.add(match.group(1))
            
        # Placeholders de lista
        for match in self.list_pattern.finditer(template):
            placeholders.add(match.group(1))
            
        return placeholders
        
    def validate_data(self, template: str, data: Dict) -> List[str]:
        """
        Valida se todos os placeholders necessários têm dados
        
        Args:
            template: Template para validação
            data: Dados disponíveis
            
        Returns:
            Lista de placeholders faltando
        """
        required = self.find_placeholders(template)
        provided = set(data.keys())
        
        missing = required - provided
        return list(missing)
        
    def clean_unused(self, text: str) -> str:
        """
        Remove placeholders não substituídos do texto
        
        Args:
            text: Texto com possíveis placeholders não substituídos
            
        Returns:
            Texto limpo
        """
        # Remove placeholders simples não substituídos
        text = self.placeholder_pattern.sub('', text)
        
        # Remove condicionais não processados
        text = self.conditional_pattern.sub('', text)
        
        # Remove listas não processadas
        text = self.list_pattern.sub('', text)
        
        # Remove linhas vazias múltiplas
        while '\n\n\n' in text:
            text = text.replace('\n\n\n', '\n\n')
            
        return text.strip()
        
    def _replace_simple_placeholders(self, text: str, data: Dict) -> str:
        """Substitui placeholders simples {key}"""
        def replacer(match):
            key = match.group(1)
            
            # Suporta navegação em dicionários aninhados
            if '.' in key:
                value = self._get_nested_value(data, key)
            else:
                value = data.get(key, match.group(0))
                
            # Converte valor para string apropriada
            if value is None:
                return match.group(0)  # Mantém o placeholder
            elif isinstance(value, datetime):
                return value.strftime('%d/%m/%Y')
            elif isinstance(value, bool):
                return 'Sim' if value else 'Não'
            else:
                return str(value)
                
        return self.placeholder_pattern.sub(replacer, text)
        
    def _process_conditionals(self, text: str, data: Dict) -> str:
        """Processa placeholders condicionais {?condition:text}"""
        def replacer(match):
            condition = match.group(1)
            content = match.group(2)
            
            # Avalia a condição
            if self._evaluate_condition(condition, data):
                # Se verdadeiro, inclui o conteúdo
                return content
            else:
                # Se falso, remove
                return ''
                
        return self.conditional_pattern.sub(replacer, text)
        
    def _process_lists(self, text: str, data: Dict) -> str:
        """Processa placeholders de lista {#list_key:template}"""
        def replacer(match):
            list_key = match.group(1)
            item_template = match.group(2)
            
            # Obtém a lista de dados
            items = data.get(list_key, [])
            if not isinstance(items, list):
                return match.group(0)  # Mantém o placeholder
                
            # Processa cada item
            results = []
            for i, item in enumerate(items):
                # Cria contexto para o item
                item_data = {
                    'item': item,
                    'index': i + 1,
                    'is_first': i == 0,
                    'is_last': i == len(items) - 1
                }
                
                # Se o item é um dicionário, adiciona seus valores
                if isinstance(item, dict):
                    item_data.update(item)
                    
                # Substitui no template do item
                item_result = self._replace_simple_placeholders(item_template, item_data)
                results.append(item_result)
                
            return '\n'.join(results)
            
        return self.list_pattern.sub(replacer, text)
        
    def _get_nested_value(self, data: Dict, key: str):
        """Obtém valor de chave aninhada (ex: 'user.name')"""
        keys = key.split('.')
        value = data
        
        for k in keys:
            if isinstance(value, dict):
                value = value.get(k)
            else:
                return None
                
        return value
        
    def _evaluate_condition(self, condition: str, data: Dict) -> bool:
        """Avalia uma condição simples"""
        # Condições suportadas:
        # - key (verifica se existe e é truthy)
        # - key=value (verifica igualdade)
        # - key!=value (verifica desigualdade)
        # - key>value (comparação numérica)
        # - key<value (comparação numérica)
        
        # Verifica operadores
        if '!=' in condition:
            key, expected = condition.split('!=', 1)
            actual = data.get(key.strip())
            return str(actual).strip() != expected.strip()
            
        elif '=' in condition:
            key, expected = condition.split('=', 1)
            actual = data.get(key.strip())
            return str(actual).strip() == expected.strip()
            
        elif '>' in condition:
            key, expected = condition.split('>', 1)
            try:
                actual = float(data.get(key.strip(), 0))
                expected = float(expected.strip())
                return actual > expected
            except:
                return False
                
        elif '<' in condition:
            key, expected = condition.split('<', 1)
            try:
                actual = float(data.get(key.strip(), 0))
                expected = float(expected.strip())
                return actual < expected
            except:
                return False
                
        else:
            # Condição simples - verifica se existe e é truthy
            value = data.get(condition.strip())
            return bool(value)
            
    def create_template_guide(self, template: str) -> str:
        """
        Cria um guia dos placeholders encontrados no template
        
        Args:
            template: Template para análise
            
        Returns:
            Texto com descrição dos placeholders
        """
        placeholders = self.find_placeholders(template)
        
        if not placeholders:
            return "Nenhum placeholder encontrado no template."
            
        guide = "PLACEHOLDERS ENCONTRADOS NO TEMPLATE:\n\n"
        
        for placeholder in sorted(placeholders):
            guide += f"- {{{placeholder}}}: [Descrição do campo {placeholder}]\n"
            
        return guide
