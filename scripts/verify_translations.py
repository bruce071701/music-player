#!/usr/bin/env python3
"""
验证翻译文件完整性的脚本
检查所有语言版本是否包含相同的字符串键
"""

import os
import xml.etree.ElementTree as ET
from pathlib import Path

def parse_strings_xml(file_path):
    """解析strings.xml文件，返回所有字符串键的集合"""
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        return {elem.get('name') for elem in root.findall('string')}
    except Exception as e:
        print(f"Error parsing {file_path}: {e}")
        return set()

def find_res_directories(base_path):
    """查找所有包含strings.xml的资源目录"""
    res_dirs = []
    base = Path(base_path)
    
    for values_dir in base.glob('app/src/main/res/values*/'):
        strings_file = values_dir / 'strings.xml'
        if strings_file.exists():
            res_dirs.append((values_dir.name, str(strings_file)))
    
    return sorted(res_dirs)

def main():
    """主函数"""
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    
    # 查找所有语言的strings.xml文件
    res_dirs = find_res_directories(project_root)
    
    if not res_dirs:
        print("No strings.xml files found!")
        return
    
    print(f"Found {len(res_dirs)} language versions:")
    
    # 解析每个文件的字符串键
    language_keys = {}
    for dir_name, file_path in res_dirs:
        keys = parse_strings_xml(file_path)
        language_keys[dir_name] = keys
        print(f"  {dir_name}: {len(keys)} strings")
    
    # 以默认语言(values)为基准
    if 'values' not in language_keys:
        print("Error: Default language (values) not found!")
        return
    
    base_keys = language_keys['values']
    print(f"\nBase language (values) has {len(base_keys)} strings")
    
    # 检查每个语言版本
    all_complete = True
    
    for lang, keys in language_keys.items():
        if lang == 'values':
            continue
            
        missing_keys = base_keys - keys
        extra_keys = keys - base_keys
        
        if missing_keys or extra_keys:
            all_complete = False
            print(f"\n❌ {lang}:")
            
            if missing_keys:
                print(f"  Missing keys ({len(missing_keys)}):")
                for key in sorted(missing_keys)[:10]:  # 只显示前10个
                    print(f"    - {key}")
                if len(missing_keys) > 10:
                    print(f"    ... and {len(missing_keys) - 10} more")
            
            if extra_keys:
                print(f"  Extra keys ({len(extra_keys)}):")
                for key in sorted(extra_keys)[:5]:  # 只显示前5个
                    print(f"    + {key}")
                if len(extra_keys) > 5:
                    print(f"    ... and {len(extra_keys) - 5} more")
        else:
            print(f"✅ {lang}: Complete")
    
    if all_complete:
        print(f"\n🎉 All {len(res_dirs)} language versions are complete!")
    else:
        print(f"\n⚠️ Some language versions have missing or extra strings")
    
    # 显示统计信息
    print(f"\n📊 Statistics:")
    print(f"  Languages: {len(res_dirs)}")
    print(f"  Base strings: {len(base_keys)}")
    
    completion_rates = []
    for lang, keys in language_keys.items():
        if lang != 'values':
            completion = len(keys & base_keys) / len(base_keys) * 100
            completion_rates.append(completion)
    
    if completion_rates:
        avg_completion = sum(completion_rates) / len(completion_rates)
        print(f"  Average completion: {avg_completion:.1f}%")

if __name__ == "__main__":
    main()