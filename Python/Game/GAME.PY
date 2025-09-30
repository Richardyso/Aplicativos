
import pygame
import sys


# Inicialização do Pygame
pygame.init()

# Cores
PRETO = (0, 0, 0)
VERMELHO = (255, 0, 0)
VERDE = (0, 255, 0)
AZUL = (0, 0, 255)

# Tamanho da tela
LARGURA = 200
ALTURA = 250


# Criação da tela
screen = pygame.display.set_mode((LARGURA, ALTURA))
pygame.display.set_caption("OLAVO")

# Posição inicial dos objetos
square_pos = [(0, 0), (50, 0), (100, 0), (150, 0)]
retangulos_pos = [(0, 50), (50, 50), (100, 50), (150, 50)]
horizontal_rect_pos = (0, 150)

# Tamanho dos objetos
square_size = 50
retangulo_width = 50
retangulo_height = 100
horizontal_rect_width = 100
horizontal_rect_height = 50

# Variáveis de controle de movimento
selected_object = None

# Função para verificar colisões entre dois objetos
def check_collision(rect1, rect2):
    x1, y1, w1, h1 = rect1
    x2, y2, w2, h2 = rect2

    # Verificar colisão entre retângulos
    if x1 < x2 + w2 and x1 + w1 > x2 and y1 < y2 + h2 and y1 + h1 > y2:
        return True

    # Verificar colisão entre retângulo e quadrado
    if isinstance(rect1, tuple) and isinstance(rect2, list):
        rx, ry, rw, rh = rect1
        qx, qy, qs = rect2

        if rx < qx + qs and rx + rw > qx and ry < qy + qs and ry + rh > qy:
            return True

    return False


# Loop principal do jogo
while True:
    # Verificação de eventos
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
        elif event.type == pygame.MOUSEBUTTONDOWN:
            # Verifica se o clique foi em algum objeto
            mouse_pos = pygame.mouse.get_pos()
            for i, pos in enumerate(square_pos):
                if pos[0] <= mouse_pos[0] <= pos[0] + square_size and \
                   pos[1] <= mouse_pos[1] <= pos[1] + square_size:
                    selected_object = ("square", i)
            for i, pos in enumerate(retangulos_pos):
                if pos[0] <= mouse_pos[0] <= pos[0] + retangulo_width and \
                   pos[1] <= mouse_pos[1] <= pos[1] + retangulo_height:
                    selected_object = ("retangulo", i)
            if horizontal_rect_pos[0] <= mouse_pos[0] <= horizontal_rect_pos[0] + horizontal_rect_width and \
               horizontal_rect_pos[1] <= mouse_pos[1] <= horizontal_rect_pos[1] + horizontal_rect_height:
                selected_object = ("horizontal_rect",)

    # Movimentação do objeto selecionado
    if selected_object:
        if pygame.key.get_pressed()[pygame.K_LEFT]:
            if selected_object[0] == "square":
                new_pos = (
                    max(square_pos[selected_object[1]][0] - 5, 0), square_pos[selected_object[1]][1])
                collision = False
                for i, pos in enumerate(retangulos_pos):
                    if check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], retangulo_width, retangulo_height)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(square_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], square_size, square_size)):
                            collision = True
                            break
                if not collision:
                    square_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "retangulo":
                new_pos = (max(
                    retangulos_pos[selected_object[1]][0] - 5, 0), retangulos_pos[selected_object[1]][1])
                collision = False
                for i, pos in enumerate(square_pos):
                    if check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(retangulos_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    retangulos_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "horizontal_rect":
                new_pos = (
                    max(horizontal_rect_pos[0] - 5, 0), horizontal_rect_pos[1])
                collision = False
                for pos in square_pos:
                    if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for pos in retangulos_pos:
                        if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    horizontal_rect_pos = new_pos
        elif pygame.key.get_pressed()[pygame.K_RIGHT]:
            if selected_object[0] == "square":
                new_pos = (min(square_pos[selected_object[1]][0] + 5,
                           LARGURA - square_size), square_pos[selected_object[1]][1])
                collision = False
                for i, pos in enumerate(retangulos_pos):
                    if check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], retangulo_width, retangulo_height)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(square_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], square_size, square_size)):
                            collision = True
                            break
                if not collision:
                    square_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "retangulo":
                new_pos = (min(retangulos_pos[selected_object[1]][0] + 5, LARGURA -
                           retangulo_width), retangulos_pos[selected_object[1]][1])
                collision = False
                for i, pos in enumerate(square_pos):
                    if check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(retangulos_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    retangulos_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "horizontal_rect":
                new_pos = (min(
                    horizontal_rect_pos[0] + 5, LARGURA - horizontal_rect_width), horizontal_rect_pos[1])
                collision = False
                for pos in square_pos:
                    if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for pos in retangulos_pos:
                        if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    horizontal_rect_pos = new_pos
        elif pygame.key.get_pressed()[pygame.K_UP]:
            if selected_object[0] == "square":
                new_pos = (square_pos[selected_object[1]][0], max(
                    square_pos[selected_object[1]][1] - 5, 0))
                collision = False
                for i, pos in enumerate(retangulos_pos):
                    if check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], retangulo_width, retangulo_height)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(square_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], square_size, square_size)):
                            collision = True
                            break
                if not collision:
                    square_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "retangulo":
                new_pos = (retangulos_pos[selected_object[1]][0], max(
                    retangulos_pos[selected_object[1]][1] - 5, 0))
                collision = False
                for i, pos in enumerate(square_pos):
                    if check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(retangulos_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    retangulos_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "horizontal_rect":
                new_pos = (horizontal_rect_pos[0], max(
                    horizontal_rect_pos[1] - 5, 0))
                collision = False
                for pos in square_pos:
                    if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for pos in retangulos_pos:
                        if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    horizontal_rect_pos = new_pos
        elif pygame.key.get_pressed()[pygame.K_DOWN]:
            if selected_object[0] == "square":
                new_pos = (square_pos[selected_object[1]][0], min(
                    square_pos[selected_object[1]][1] + 5, ALTURA - square_size))
                collision = False
                for i, pos in enumerate(retangulos_pos):
                    if check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], retangulo_width, retangulo_height)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(square_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], square_size, square_size), (pos[0], pos[1], square_size, square_size)):
                            collision = True
                            break
                if not collision:
                    square_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "retangulo":
                new_pos = (retangulos_pos[selected_object[1]][0], min(
                    retangulos_pos[selected_object[1]][1] + 5, ALTURA - retangulo_height))
                collision = False
                for i, pos in enumerate(square_pos):
                    if check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for i, pos in enumerate(retangulos_pos):
                        if i != selected_object[1] and check_collision((new_pos[0], new_pos[1], retangulo_width, retangulo_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    retangulos_pos[selected_object[1]] = new_pos
            elif selected_object[0] == "horizontal_rect":
                new_pos = (horizontal_rect_pos[0], min(
                    horizontal_rect_pos[1] + 5, ALTURA - horizontal_rect_height))
                collision = False
                for pos in square_pos:
                    if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], square_size, square_size)):
                        collision = True
                        break
                if not collision:
                    for pos in retangulos_pos:
                        if check_collision((new_pos[0], new_pos[1], horizontal_rect_width, horizontal_rect_height), (pos[0], pos[1], retangulo_width, retangulo_height)):
                            collision = True
                            break
                if not collision:
                    horizontal_rect_pos = new_pos

    # Preenchimento da tela com a cor preta
    screen.fill(PRETO)

    # Desenho dos objetos
    for pos in square_pos:
        pygame.draw.rect(
            screen, VERMELHO, (pos[0], pos[1], square_size, square_size))
    for pos in retangulos_pos:
        pygame.draw.rect(
            screen, VERDE, (pos[0], pos[1], retangulo_width, retangulo_height))
    pygame.draw.rect(
        screen, AZUL, (horizontal_rect_pos[0], horizontal_rect_pos[1], horizontal_rect_width, horizontal_rect_height))

    # Atualização da tela
    pygame.display.flip()
