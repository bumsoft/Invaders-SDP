# 개발바닥(DevFloor)

## Team Introduction
> Our team plans to design levels based on difficulty and level by strengthening enemies and creating obstacles to hinder allies.

저희 팀은 난이도와 레벨에 따라 적군을 강화하고 아군을 방해하는 장애물을 생성하여 레벨을 설계할 계획입니다.


- Team leader
    - `Sangjin Lee`

- Team member
    - `Jihwan Choi`
    - `Yongjin Kwon`
    - `Jihun Yeo`
    - `Heonjun Chu`
    - `Uijae Kim`
    - `Jueun Seo`

## Team Requirements
    - Level Design

### - Instruct

- `Sangjin Lee` : Spider web development
- `Jihwan Choi` : Game difficulty adjustment
- `Yongjin Kwon` : Developing endless steps
- `Jihun Yeo`  : Development of obstructions to vision
- `Heonjun Chu` : Development of enemy reinforcement proportional to level increase
- `Uijae Kim` : Change enemy bullets
- `Jueun Seo` : Bullet obstacle development

## Detailed Requirements

- Difficulty adjustment (overall)
  Initial difficulty selection: You can adjust the overall difficulty by selecting `Easy`, `Normal`, or `Hard` at the start of the game.
  Difficulty increase by time or score: The game's difficulty will gradually increase as the game time passes or a certain score is reached.

- Difficulty adjustment factors

  - Enemy speed, number of bullets, damage, bullet firing frequency:
    As the difficulty increases, enemies become faster, fire more bullets, inflict greater damage, and fire less frequently.

  - Score conversion by difficulty:
    The higher the difficulty (e.g. `Hard` mode), the more weight is added to the score you get at each level.
  - Enemy health increase by level:
    As the level increases, enemies have more health, making them harder to destroy.
  - Enemy bullet size increase:
    As the difficulty increases, the size of enemy bullets increases, making evasion more difficult.

- New Features
  - Obstacles (Block class):
    Create a new `Block` class to design obstacles that enemy and friendly projectiles cannot pass through. These obstacles appear on the stage and act as obstacles in battle.
  - Harder Enemy Difficulty 1:
    Modify the `shoot` method of the `EnemyShipFormation` class so that enemies fire two bullets at once or multiple enemies fire bullets at the same time as the level increases.

  - Harder Enemy Difficulty 2:
    Modify the `destroy` method of the `EnemyShipFormation` class so that enemies are not destroyed immediately as the level increases, and increase the damage to enemy projectiles from 1 to 2.

  - Ally Speed Limit (Web class):
    Remove the `final` value of the `speed` property of the `Ship` entity, and create a new `Web` class to design a platform that slows down friendly units when they step on it.

  - Endless Stages (Infinite Levels):
    Added a `levelDesign` method in the `GameSettings` class to change the game stages limited to 7 stages to unlimited, and set the difficulty to increase exponentially after stage 5.

  - Adjusting the score coefficient according to difficulty:
    Added methods to the `EnemyShip` and `EnemyShipFormation` classes to set different score acquisition coefficients according to the difficulty (`EASY`, `NORMAL`, `HARD`).

  - Obstructing Elements:
    Using `JFrame`, add elements that block the enemy's vision or limit the ally's vision as the level increases, making it difficult for the player to move and attack.




- 난이도 조절 (전체)
초기 난이도 선택: 게임 시작 시 `Easy`, `Normal`, `Hard` 중 하나를 선택하여 전체적인 난이도를 조절할 수 있습니다.
시간 또는 점수에 따른 난이도 증가: 게임 시간이 흐르거나 특정 점수에 도달하면 게임의 난이도가 점진적으로 증가합니다.

- 난이도 조절 요소

  - 적군 속도, 총알 수, 데미지, 총알 발사 빈도:
난이도가 올라갈수록 적군의 속도가 빨라지고, 더 많은 총알을 발사하며, 더 큰 데미지를 주고, 발사 빈도도 짧아집니다.

  - 난이도에 따른 점수 변환:
  난이도가 높을수록 (예: `Hard` 모드) 각 레벨에서 얻는 점수에 가중치가 붙습니다.
  - 레벨 증가에 따른 적군 체력 증가:
  레벨이 오를수록 적군의 체력이 더 많아져 파괴하기 어려워집니다.
  - 적군 총알 굵기 증가:
  난이도가 높아지면 적군의 총알 크기가 커져 회피가 더 어려워집니다.

- 새로운 기능
  - 장애물 (Block 클래스):
        `Block` 클래스를 새로 생성하여 적군과 아군의 발사체가 통과할 수 없는 장애물을 설계합니다. 이러한 장애물은 스테이지에 등장하여 전투에 방해 요소로 작용합니다.
  - 어려워진 적군 난이도 1:
      `EnemyShipFormation` 클래스의 `shoot` 메소드를 수정하여 레벨이 올라가면 적군이 한 번에 두 발의 총알을 발사하거나, 여러 적이 동시에 총알을 발사하도록 설정합니다.

  - 어려워진 적군 난이도 2:
      `EnemyShipFormation` 클래스의 `destroy` 메소드를 수정하여 레벨이 올라갈수록 적군이 즉시 파괴되지 않도록 하며, 적군이 발사하는 투사체의 데미지를 1에서 2로 증가시킵니다.

  - 아군 속도 제한 (Web 클래스):
      `Ship` 엔터티의 `speed` 속성의 `final` 값을 제거하고, 새로운 `Web` 클래스를 생성하여 아군이 밟으면 속도가 느려지는 발판을 설계합니다.

  - 끝없는 단계 (무한 레벨):
      `GameSettings` 클래스 내에 `levelDesign` 메소드를 추가하여, 기존 7단계로 제한된 게임 단계를 무제한으로 변경하고, 5단계 이후부터는 난이도가 기하급수적으로 상승하도록 설정합니다.

  - 난이도에 따른 점수 계수 조정:
      `EnemyShip` 및 `EnemyShipFormation` 클래스에 메소드를 추가하여 난이도 (`EASY`, `NORMAL`, `HARD`)에 따라 점수 획득 계수를 다르게 설정합니다.

  - 시야 방해 요소:
      `JFrame`을 활용하여 레벨이 올라갈수록 적군의 시야를 가리거나, 아군의 시야를 제한하여 플레이어의 이동 및 공격을 어렵게 만드는 요소를 추가합니다.





