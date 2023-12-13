package ReciteWords1;

public class Word {
        String word;
        String chinese;
        int count = 0;//答对次数
        int never = 0;//标记不再显示
        int no = 0;


    public Word(String word, String chinese, int no) {
        this.word = word;
        this.chinese = chinese;
        this.no = no;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNever() {
        return never;
    }

    public void setNever(int never) {
        this.never = never;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

}
