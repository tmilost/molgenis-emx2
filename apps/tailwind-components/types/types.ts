import type { IColumn } from "../../metadata-utils/dist";
import type { columnValue } from "../../metadata-utils/src/types";

export type Resp<T> = {
  data: Record<string, T[]>;
};

export interface Schema {
  id: string;
  label: string;
  description: string;
}
export interface INode {
  name: string;
  description?: string;
}

export interface IValueLabel {
  value: any;
  label?: string;
}

export interface ITreeNode extends INode {
  children:
  ITreeNode[];
}

export interface ITreeNodeState extends ITreeNode {
  /* if a node should be shown, used for search filter */
  visible?: boolean;
  /* if a node is selected, intermediate or unselected*/
  selected?: SelectionState; //'unselected','selected','intermediate'
  /* if a node should be shown expanded */
  expanded?: boolean;
  /* helper to quickly navigate to parent node */
  parent?: string;
  /* extension of children */
  children: ITreeNodeState[];
  /* if a node is selectable */
  selectable: boolean
}

export type SelectionState = "selected" | "intermediate" | "unselected";

export type ButtonType =
  | "primary"
  | "secondary"
  | "tertiary"
  | "outline"
  | "disabled"
  | "filterWell";

export type ButtonSize = "tiny" | "small" | "medium" | "large";

export type ButtonIconPosition = "left" | "right";

export type INotificationType =
  | "light"
  | "dark"
  | "success"
  | "error"
  | "warning"
  | "info";

export type sortDirection = "ASC" | "DESC";
export interface ITableSettings {
  page: number;
  pageSize: number;
  orderby: {
    column: string;
    direction: sortDirection
  };
  search: string;
}

export interface ISectionField {
  meta: IColumn;
  value: any;
}

export interface ISection {
  meta: IColumn;
  fields: ISectionField[];
}

export interface IFile {
  id?: string;
  size?: number;
  extension?: string;
  url?: string;
}

export interface IDocumentation {
  name: string;
  description: string;
  url: string;
  file: IFile;
}

export interface IRadioOptionsData {
  value: columnValue;
  label?: string;
  checked?: boolean | undefined;
}

  