# -*- coding: utf-8 -*-
"""
Módulo core com lógica de negócio
"""

from .contract_builder import ContractBuilder
from .proposal_builder import ProposalBuilder
from .language_selector import LanguageSelector
from .placeholders import PlaceholderManager

__all__ = [
    'ContractBuilder',
    'ProposalBuilder', 
    'LanguageSelector',
    'PlaceholderManager'
]
