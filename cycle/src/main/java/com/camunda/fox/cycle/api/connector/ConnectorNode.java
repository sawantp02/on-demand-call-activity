package com.camunda.fox.cycle.api.connector;


public class ConnectorNode {
  protected String path;
  protected String name;
  protected String displayName;
  private ConnectorNodeType type;
  
  public enum ConnectorNodeType {
    FILE,
    FOLDER
  }
  
  public ConnectorNode() {
  }
  
  public ConnectorNode(String path, String name) {
    this.setPath(path);
    this.setName(name);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
  
  /**
   * Internal Name of this node, must be unique for the hierarchy level
   * @return
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * The displayed (e.g. for UI purposes) name of this node 
   * @return
   */
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ConnectorNodeType getType() {
    return type;
  }

  public void setType(ConnectorNodeType type) {
    this.type = type;
  }
  
}
