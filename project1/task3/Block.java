class Block {
  int offset;
  int size;
  Block nextBlock;

  public Block(int offset, int size, Block next) {
    this.offset = offset;
    this.size = size;
    this.nextBlock = next;
  }

  public Block(int offset, int size) {
    this.offset = offset;
    this.size = size;
  }
}

