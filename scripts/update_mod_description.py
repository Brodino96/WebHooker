#!/usr/bin/env python3

import json
import os
import re
import sys

def main():
    print("=== Mod Description Updater ===")
    
    project_root = get_project_root()
    gradle_properties = os.path.join(project_root, 'gradle.properties')
    fabric_json = os.path.join(project_root, 'fabric', 'src', 'main', 'resources', 'fabric.mod.json')
    forge_toml = os.path.join(project_root, 'forge', 'src', 'main', 'resources', 'META-INF', 'mods.toml')
    
    print(f"Reading mod_description from gradle.properties...")
    description = read_mod_description(gradle_properties)
    print(f"Found description: \"{description}\"\n")
    
    print("Updating configuration files...")
    update_fabric_json(fabric_json, description)
    update_forge_toml(forge_toml, description)
    
    print("\n[SUCCESS] All files updated successfully!")


def get_project_root():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    return os.path.dirname(script_dir)


def read_mod_description(gradle_properties_path):
    if not os.path.exists(gradle_properties_path):
        print(f"ERROR: gradle.properties not found at {gradle_properties_path}")
        sys.exit(1)
    
    with open(gradle_properties_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            
            if not line or line.startswith('#'):
                continue
            
            if line.startswith('mod_description='):
                description = line.split('=', 1)[1].strip()
                if description:
                    return description
                else:
                    print("ERROR: mod_description is empty in gradle.properties")
                    sys.exit(1)
    
    print("ERROR: mod_description not found in gradle.properties or is commented out")
    sys.exit(1)


def update_fabric_json(fabric_json_path, description):
    if not os.path.exists(fabric_json_path):
        print(f"ERROR: fabric.mod.json not found at {fabric_json_path}")
        sys.exit(1)
    
    try:
        with open(fabric_json_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        data['description'] = description
        
        with open(fabric_json_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
            f.write('\n')  # Add trailing newline
        
        print(f"[OK] Updated fabric.mod.json")
    except json.JSONDecodeError as e:
        print(f"ERROR: Failed to parse fabric.mod.json: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"ERROR: Failed to update fabric.mod.json: {e}")
        sys.exit(1)


def update_forge_toml(forge_toml_path, description):
    if not os.path.exists(forge_toml_path):
        print(f"ERROR: mods.toml not found at {forge_toml_path}")
        sys.exit(1)
    
    try:
        with open(forge_toml_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Replace description = "" or description = "..." with the new description
        # This pattern matches: description = "anything" (with or without content)
        pattern = r'^description\s*=\s*"[^"]*"'
        replacement = f'description = "{description}"'
        
        new_content, count = re.subn(pattern, replacement, content, flags=re.MULTILINE)
        
        if count == 0:
            print("ERROR: Could not find 'description' field in mods.toml")
            sys.exit(1)
        
        with open(forge_toml_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        
        print(f"[OK] Updated mods.toml")
    except Exception as e:
        print(f"ERROR: Failed to update mods.toml: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
