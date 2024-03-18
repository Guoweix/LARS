#!/bin/bash

# 函数用于递归遍历文件夹并删除 .class 文件
function delete_class_files {
    local dir=$1

    # 遍历文件夹内的文件和子文件夹
    for file in "$dir"/*; do
        if [[ -d "$file" ]]; then
            # 如果是文件夹，则递归调用函数
            delete_class_files "$file"
        elif [[ -f "$file" && ${file##*.} == "class" ]]; then
            # 如果是 .class 文件，则删除
            echo "删除文件: $file"
            rm "$file"
        fi
    done
}

# 指定要删除 .class 文件的文件夹路径
folder_path="./"

# 调用函数删除 .class 文件
delete_class_files "$folder_path"