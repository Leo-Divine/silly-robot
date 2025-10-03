pub struct Match {
  pub start_index: usize,
  pub end_index: usize
}

impl Match {
  pub fn new(start_index: usize, end_index: usize) -> Self {
    Self { start_index, end_index }
  }
}

pub fn find(string: &str, pattern: &str) -> Option<Match> {
  for i in 0..string.len() - pattern.len() + 1 {
    let test = &string[i..(i + pattern.len())];
    if test == pattern {
      return Some(Match::new(i, i + pattern.len()));
    }
  }

  return None
}

pub fn find_after(string: &str, pattern: &str, after_index: usize) -> Option<Match> {
  for i in after_index..string.len() - pattern.len() {
    if &string[i..(i + pattern.len())] == pattern {
      return Some(Match::new(i, i + pattern.len()));
    }
  }

  return None
}