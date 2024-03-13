#!/bin/bash

# 指定源文件夹路径
source_folder="./"


# 遍历源文件夹中的文件
for file in "$source_folder"/*; do
    if [[ -f "$file" ]]; then
        # 获取文件名和扩展名
        filename=$(basename "$file")
        extension="${filename##*.}"

        # 判断文件扩展名是否为 .txt 或 .cpp
        if [[ "$extension" == "txt" || "$extension" == "java" || "$extension"=="properties" || "$extension"=="xml"]]; then
            # 转换文件编码为 UTF-8
            iconv -f GBK -t UTF-8 "$file" > "$target_folder/$filename"
            echo "转换文件: $file"
        fi
    fi
done