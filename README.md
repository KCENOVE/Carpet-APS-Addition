# Carpet APS Addition

一个 APS 服务器自用的 Carpet 扩展，只是从各个扩展中提取出来用得上的功能，以免加一堆扩展引入很多不需要的东西。

来源引用见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

## 规则

### 易碎深板岩(softDeepslate)

启用后深板岩及其变种与石头或圆石有相同的硬度

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`

*来自：[Carpet-Org-Addition](https://github.com/fcsailboat/Carpet-Org-Addition)*

### 抑制方块破坏位置不匹配警告(suppressionMismatchInDestroyBlockPosWarn)

阻止在日志中输出“Mismatch in destroy block pos: {} {}”警告

- 类型：`布尔值`
- 默认值：`false`
- 参考选项：`true`，`false`

*来自：[Carpet-Org-Addition](https://github.com/fcsailboat/Carpet-Org-Addition)*

## 命令

### 生存/旁观切换(spectator)

所有玩家可用

- `spectator`：在当前游戏模式和旁观模式之间切换，切换回来时恢复原来的游戏模式并传送回原位置
    - 如果当前处于旁观模式且没有返回点，即不是通过本命令进入的旁观模式，不执行任何操作
- `spectator tp <player>`：传送到指定在线玩家的位置，只能在通过本命令进入的旁观模式下使用

*部分来自：[Carpet-Org-Addition](https://github.com/fcsailboat/Carpet-Org-Addition)*

## 配置

配置文件为 `config/carpet-aps-addition.json`

### 示例配置

```json
{
  "command_aliases": {
    "spectator": ["spec", "s"]
  }
}
```
